const fs = require('node:fs');
const path = require('node:path');
const os = require('node:os');
const { spawn, spawnSync } = require('node:child_process');

const project = path.resolve(__dirname, '..');
const jar = path.join(project, 'ruoyi-admin', 'target', 'ruoyi-admin.jar');
if (!fs.existsSync(jar)) {
  console.error('未找到后端程序，请先在项目根目录执行 Maven 打包。');
  process.exit(1);
}
const env = { ...process.env };
env.SERVER_ADDRESS ||= '127.0.0.1';
const secretPath = path.join(os.homedir(), '.codex', 'campus-bike-tools', 'campus-local-secrets.json');
if ((!env.DB_USERNAME || env.DB_PASSWORD === undefined || !env.APP_TOKEN_SECRET) && fs.existsSync(secretPath) && process.platform === 'win32') {
  const ps = [
    "$ErrorActionPreference = 'Stop'",
    '$j = Get-Content -Raw -LiteralPath $env:CAMPUS_SECRET_FILE | ConvertFrom-Json',
    'function reveal($v) { $p=ConvertTo-SecureString -String $v; $b=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($p); try { [Runtime.InteropServices.Marshal]::PtrToStringBSTR($b) } finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($b) } }',
    '@{username=$j.username; password=(reveal $j.password); token=(reveal $j.token); backupLoginPath=$j.backupLoginPath} | ConvertTo-Json -Compress'
  ].join('; ');
  const powershell = path.join(env.SystemRoot || 'C:\\Windows', 'System32', 'WindowsPowerShell', 'v1.0', 'powershell.exe');
  const result = spawnSync(powershell, ['-NoProfile', '-NonInteractive', '-EncodedCommand', Buffer.from(ps, 'utf16le').toString('base64')], {
    env: { ...env, CAMPUS_SECRET_FILE: secretPath }, encoding: 'utf8', windowsHide: true
  });
  if (result.status !== 0) { console.error('无法读取当前 Windows 用户的本地数据库配置。'); process.exit(1); }
  try {
    const local = JSON.parse(result.stdout.replace(/^\uFEFF/, ''));
    env.DB_USERNAME ||= local.username;
    if (env.DB_PASSWORD === undefined) env.DB_PASSWORD = local.password;
    env.APP_TOKEN_SECRET ||= local.token;
    if (!env.CAMPUS_BACKUP_LOGIN_PATH && local.backupLoginPath) env.CAMPUS_BACKUP_LOGIN_PATH = local.backupLoginPath;
  } catch (_) { console.error('本地数据库配置格式无效。'); process.exit(1); }
}
if (!env.DB_USERNAME || env.DB_PASSWORD === undefined || !env.APP_TOKEN_SECRET) {
  console.error('请先设置 DB_USERNAME、DB_PASSWORD 和 APP_TOKEN_SECRET，再启动后端。');
  process.exit(1);
}
if (!env.MYSQL_DUMP_PATH && process.platform === 'win32') {
  const dump = path.join(env.ProgramFiles || 'C:\\Program Files', 'MySQL', 'MySQL Server 8.0', 'bin', 'mysqldump.exe');
  if (fs.existsSync(dump)) env.MYSQL_DUMP_PATH = dump;
}
const java = env.JAVA_HOME ? path.join(env.JAVA_HOME, 'bin', process.platform === 'win32' ? 'java.exe' : 'java') : 'java';
console.log('正在启动校园助力车后端，默认地址：http://localhost:8080');
const child = spawn(java, ['-jar', jar], { cwd: project, env, stdio: 'inherit', windowsHide: true });
child.on('error', () => { console.error('无法启动 Java，请检查 JDK 17 是否可用。'); process.exitCode = 1; });
child.on('exit', code => { process.exitCode = code === null ? 1 : code; });

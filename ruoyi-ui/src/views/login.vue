<template>
  <div class="login">
    <section class="login-intro" aria-label="校园助力车介绍">
      <div class="intro-mark">CAMPUS / RIDE</div>
      <div><p class="intro-kicker">A BETTER WAY AROUND CAMPUS</p><h1>下一站，<br>从这里出发。</h1><p class="intro-copy">让校园出行更轻松，也让每一次借还更清晰。</p></div>
      <div class="route-art" aria-hidden="true"><span></span><i></i><b></b><em>01 — 02</em></div>
      <div class="intro-foot">校园出行服务 <span>·</span> 简单可靠</div>
    </section>
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="form-eyebrow">WELCOME BACK</div>
      <h3 class="title">{{ title }}</h3>
      <p class="form-subtitle">登录账号，开始使用校园出行服务。</p>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          size="large"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码，点击可刷新"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住账号</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
  code: "",
  uuid: ""
})

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
// 验证码开关
const captchaEnabled = ref(true)
// 注册开关
const register = ref(false)
const redirect = ref(undefined)

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      // 只保存账号，不在浏览器中保存密码。
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 })
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 })
      } else {
        // 否则移除
        Cookies.remove("username")
        Cookies.remove("password")
        Cookies.remove("rememberMe")
      }
      Cookies.remove("password")
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur]
          }
          return acc
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

function getCookie() {
  const username = Cookies.get("username")
  const rememberMe = Cookies.get("rememberMe")
  loginForm.value.username = username || ""
  loginForm.value.password = ""
  loginForm.value.rememberMe = rememberMe === "true"
  Cookies.remove("password")
}

getCode()
getCookie()
</script>

<style lang="scss" scoped>
.login { min-height:100vh; display:flex; align-items:center; justify-content:center; gap:clamp(40px,7vw,110px); padding:48px 8vw 70px; background:#eaf2ec; color:#173d37; }
.login-intro { position:relative; display:flex; flex-direction:column; justify-content:space-between; width:min(52%,620px); min-height:570px; padding:40px 44px; overflow:hidden; border-radius:16px; background:#123f3b; color:#edf8ee; }
.intro-mark,.intro-kicker { position:relative; z-index:1; font-size:10px; font-weight:700; letter-spacing:.2em; color:#94debe; }.intro-kicker { margin:0 0 20px; }.login-intro h1 { position:relative; z-index:1; margin:0; font-size:clamp(34px,4.4vw,58px); line-height:1.3; letter-spacing:.04em; }.intro-copy { position:relative; z-index:1; margin:22px 0 0; color:#b6d6c5; font-size:14px; line-height:1.8; }.intro-foot { position:relative; z-index:1; color:#9abfb0; font-size:12px; letter-spacing:.08em; }.intro-foot span { padding:0 8px; }.route-art { position:absolute; right:-80px; bottom:-110px; width:400px; height:400px; border:1px solid #3c7466; border-radius:50%; }.route-art span,.route-art i { position:absolute; border:1px solid #3c7466; border-radius:50%; inset:38px; }.route-art i { inset:78px; }.route-art b { position:absolute; top:55px; left:96px; width:12px; height:12px; border-radius:50%; background:#a5e6bb; }.route-art em { position:absolute; top:170px; left:137px; color:#77b9a0; font-style:normal; font-size:11px; letter-spacing:.18em; }
.login-form { width:min(100%,390px); padding:38px 36px 20px; border:1px solid #dce9df; border-radius:14px; background:#fff; box-shadow:0 18px 50px rgba(24,61,55,.08); z-index:1; }.form-eyebrow { color:#2a947b; font-size:10px; font-weight:700; letter-spacing:.18em; }.title { margin:12px 0 8px; color:#173d37; font-size:25px; }.form-subtitle { margin:0 0 30px; color:#82958c; font-size:13px; }.login-form .el-input { height:42px; }.input-icon { width:14px; }.login-code { width:37%; height:40px; float:right; }.login-code-img { height:40px; max-width:100%; padding-left:10px; cursor:pointer; }.el-login-footer { position:fixed; bottom:12px; left:0; width:100%; text-align:center; color:#77958a; font-size:11px; letter-spacing:.06em; }
.login-form :deep(.el-button--primary) { --el-button-bg-color:#19775f; --el-button-border-color:#19775f; --el-button-hover-bg-color:#238b70; --el-button-hover-border-color:#238b70; }
@media(max-width:800px){.login{flex-direction:column;gap:18px;padding:20px 16px 55px}.login-intro{width:min(100%,480px);min-height:220px;padding:25px 26px}.login-intro h1{font-size:30px}.intro-kicker{margin-bottom:10px}.intro-copy{margin-top:10px}.intro-foot{display:none}.route-art{width:260px;height:260px;right:-85px;bottom:-120px}.login-form{width:min(100%,480px);padding:28px 26px 12px}.form-subtitle{margin-bottom:24px}}
</style>

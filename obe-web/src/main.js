import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import Icon from './components/Icon.vue'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus) // 去掉损坏的locale配置修复日期组件
app.component('Icon', Icon)
app.mount('#app')

<template>
  <div class="kp-layout">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom:12px">
      <template #title>
        MaxKB 知识库（账号 <b>admin</b> / <b>admin123</b>，首次登录后自动记住）
      </template>
    </el-alert>
    <div class="kp-iframe-wrapper">
      <iframe src="/admin/" class="kp-iframe" title="MaxKB知识库管理" />
    </div>
  </div>
</template>

<style scoped>
.kp-layout { max-width:100%; margin:0 auto; }
.kp-iframe-wrapper { width:100%; height:85vh; border:1px solid #dcdfe6; border-radius:4px; overflow:hidden; }
.kp-iframe { width:100%; height:100%; border:none; }
</style>

import { fileURLToPath, URL } from 'node:url';
import { existsSync } from 'node:fs';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import copy from 'rollup-plugin-copy';

const getFileFromRepo = (file: string) =>
  existsSync(fileURLToPath(new URL(`../node_modules/${file}`, import.meta.url)))
    ? fileURLToPath(new URL(`../node_modules/${file}`, import.meta.url))
    : fileURLToPath(new URL(`./node_modules/${file}`, import.meta.url));

// eslint-disable-next-line prefer-const
let config = defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          compatConfig: {
            MODE: 2,
          },
        },
      },
    }),
    copy({
      targets: [
        {
          src: [
            `${require('swagger-ui-dist').getAbsoluteFSPath()}/*.{js,css,html,png}`,
            `!${require('swagger-ui-dist').getAbsoluteFSPath()}/**/index.html`,
            getFileFromRepo('axios/dist/axios.min.js'),
            fileURLToPath(new URL('./src/main/webapp/swagger-ui/index.html', import.meta.url)),
          ],
          dest: 'target/classes/static/swagger-ui',
        },
      ],
      hook: 'writeBundle',
    }),
  ],
  root: fileURLToPath(new URL('./src/main/webapp/', import.meta.url)),
  publicDir: fileURLToPath(new URL('./target/classes/static/public', import.meta.url)),
  cacheDir: fileURLToPath(new URL('./target/.vite-cache', import.meta.url)),
  build: {
    emptyOutDir: true,
    outDir: fileURLToPath(new URL('./target/classes/static/', import.meta.url)),
    rollupOptions: {
      input: {
        app: fileURLToPath(new URL('./src/main/webapp/index.html', import.meta.url)),
      },
    },
  },
  resolve: {
    alias: {
      vue: '@vue/compat/dist/vue.esm-bundler.js',
      '@': fileURLToPath(new URL('./src/main/webapp/app/', import.meta.url)),
    },
  },
  define: {
    I18N_HASH: '"generated_hash"',
    SERVER_API_URL: '"/"',
    APP_VERSION: `"${process.env.APP_VERSION ? process.env.APP_VERSION : 'DEV'}"`,
  },
  server: {
    proxy: Object.fromEntries(
      ['/api', '/management', '/v3/api-docs', '/h2-console'].map(res => [
        res,
        {
          target: 'http://localhost:8080',
        },
      ]),
    ),
  },
});

// jhipster-needle-add-vite-config - JHipster will add custom config

export default config;

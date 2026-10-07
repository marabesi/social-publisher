import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'en-US',
  title: 'Social Publisher',
  description:
    'A Kotlin/JVM CLI that schedules and publishes posts to social networks from your terminal.',
  base: process.env.DOCS_BASE || '/',
  cleanUrls: true,
  lastUpdated: true,
  themeConfig: {
    nav: [
      { text: 'Guide', link: '/guide/installation' },
      { text: 'Reference', link: '/reference/cli' },
      { text: 'Architecture', link: '/architecture' },
      {
        text: 'GitHub',
        link: 'https://github.com/marabesi/social-publisher',
      },
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Introduction', link: '/' },
          { text: 'Installation', link: '/guide/installation' },
          { text: 'Configuration', link: '/guide/configuration' },
          { text: 'Managing posts', link: '/guide/posts' },
          { text: 'Scheduling posts', link: '/guide/scheduling' },
          { text: 'Publishing posts', link: '/guide/publishing' },
        ],
      },
      {
        text: 'Reference',
        items: [
          { text: 'CLI reference', link: '/reference/cli' },
          { text: 'Architecture', link: '/architecture' },
        ],
      },
    ],
    search: {
      provider: 'local',
    },
    socialLinks: [
      {
        icon: 'github',
        link: 'https://github.com/marabesi/social-publisher',
      },
    ],
    footer: {
      message: 'Released under the Apache-2.0 License.',
      copyright: 'Copyright © Matheus Marabesi',
    },
    outline: {
      level: [2, 3],
    },
  },
})

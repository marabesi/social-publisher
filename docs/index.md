---
layout: home

hero:
  name: Social Publisher
  text: Schedule and publish posts from your terminal
  tagline: A small Kotlin/JVM CLI that stores your posts and schedules in local CSV files, then publishes them to X (Twitter) when the time comes.
  actions:
    - theme: brand
      text: Get started
      link: /guide/installation
    - theme: alt
      text: Configure the tool
      link: /guide/configuration
    - theme: alt
      text: View on GitHub
      link: https://github.com/marabesi/social-publisher

features:
  - title: Local-first
    details: Posts, schedules and credentials live in plain files under data/. No database or server to run.
  - title: Schedule once, publish later
    details: Create a post, attach a publish date, then run the poster routine to send everything that is due.
  - title: Hexagonal by design
    details: The core is framework-free and talks to storage and social networks through ports, so new adapters are easy to add.
  - title: Twitter today
    details: X (Twitter) is supported through OAuth 1.0a, with LinkedIn on the roadmap.
---

## Getting started

1. first step is to find twitter credentials
2. create a configuration for social-publisher
3. create a post
4. schedule it to be sent

```plantuml
node social_app [
    social app
    ---
    java cli application
    - manages data through CLI
    - manages posts
    - manages configuration
    - manages scheduled posts
]

database social_data [
  Social data (csv file)
  ---
  - posts to be published
  - scheduled times for each post
]

file configuration as "Configuration engine, database type,\n social media credentials"

cloud twitter_api [
    twitter api
]

social_app --> social_data: [pulls data and writes to]
social_app --> twitter_api: creates tweets
social_app --> configuration
```

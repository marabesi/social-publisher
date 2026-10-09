#!/bin/bash

function cleanup() {
  rm -rf ./data
  mkdir ./data
}

function set_up_before_script() {
  cleanup
  ./distribute.sh
}

function tear_down_after_script() {
  cleanup
}

###############################################
## configuration
###############################################
function test_can_create_configuration() {
  local config=$(cat cli/e2e/config.example.json)
  output=$(sh ./social/bin/social configuration -c "$config")
  local expected="Configuration has been stored"
  assert_contains "${expected}" "${output}"
}

function test_does_not_accept_empty_configuration() {
  local config=$(cat cli/e2e/config.example.json)
  output=$(sh ./social/bin/social configuration -c "")
  local expected="Missing required fields"
  assert_contains "${expected}" "${output}"
}

###############################################
## creating posts
###############################################
function test_creates_post() {
  output=$(sh ./social/bin/social post -c "random")
  local expected="Post has been created"
  assert_contains "${expected}" "${output}"
}

function test_list_created_posts() {
  output=$(sh ./social/bin/social post -l)
  local expected="1. random"
  assert_contains "${expected}" "${output}"
}

###############################################
## scheduling
###############################################
function test_schedule_a_post_for_twitter() {
    output=$(sh ./social/bin/social scheduler create -p "1" -d "2090-10-02T09:00:00Z" -s "TWITTER")
    local expected="Post has been scheduled using UTC timezone"
    assert_contains "${expected}" "${output}"
}

function test_list_scheduled_post() {
    output=$(sh ./social/bin/social scheduler list)
    local expected="1. Post with id 1 will be published on 2090-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_list_scheduled_post_with_start_and_end_data() {
    output=$(sh ./social/bin/social scheduler list  --start-date "2026-10-02T09:00:00Z" --end-date "2091-10-02T09:00:00Z")
    local expected="1. Post with id 1 will be published on 2090-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_order_scheduled_posts_by_publish_date_desc() {
    sh ./social/bin/social post -c "random 2" > /dev/null
    sh ./social/bin/social scheduler create -p "2" -d "2089-10-02T09:00:00Z" -s "TWITTER" > /dev/null

    output=$(sh ./social/bin/social scheduler list --order-by "publish_date=desc")
    local expected="1. Post with id 1 will be published on 2090-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_filter_scheduled_posts_by_date_parts() {
    output=$(sh ./social/bin/social scheduler list --filter "day=02&month=10&year=2089")
    local expected="1. Post with id 2 will be published on 2089-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_filter_scheduled_posts_by_any_property() {
    output=$(sh ./social/bin/social scheduler list --filter "post.text=random 2")
    local expected="1. Post with id 2 will be published on 2089-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_filter_and_order_scheduled_posts_together() {
    output=$(sh ./social/bin/social scheduler list --filter "day=02&month=10" --order-by "publish_date=asc")
    local expected="1. Post with id 2 will be published on 2089-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

function test_delete_scheduled_post_by_its_id() {
    output=$(sh ./social/bin/social scheduler delete -id "1")
    local expected="Schedule 1 has been removed from post 1"
    assert_contains "${expected}" "${output}"

}

###############################################
## timezone aware scheduling
###############################################
function test_schedule_with_local_time_converted_to_utc_using_configured_timezone() {
    sh ./social/bin/social configuration -c '{"storage":"csv","fileName":"tz-e2e","timezone":"Europe/Madrid"}' > /dev/null
    sh ./social/bin/social post -c "tz post" > /dev/null

    output=$(sh ./social/bin/social scheduler create -p "1" -d "2022-10-02T09:00:00" -s "TWITTER")
    local expected="Post has been scheduled using Europe/Madrid timezone"
    assert_contains "${expected}" "${output}"

    output=$(sh ./social/bin/social scheduler list)
    local expected="1. Post with id 1 will be published on 2022-10-02T07:00:00Z"
    assert_contains "${expected}" "${output}"
}

###############################################
## searching posts
###############################################
function test_search_posts_by_fuzzy_text() {
    sh ./social/bin/social configuration -c '{"storage":"csv","fileName":"search-e2e","timezone":"UTC"}' > /dev/null
    sh ./social/bin/social post -c "hello desktop" > /dev/null
    sh ./social/bin/social post -c "release notes" > /dev/null

    output=$(sh ./social/bin/social post -l --search "hlo dsk")
    local expected="1. hello desktop"
    assert_contains "${expected}" "${output}"
}

function test_search_posts_by_more_than_one_id() {
    output=$(sh ./social/bin/social post -l --ids "1,2")
    local expected="2. release notes"
    assert_contains "${expected}" "${output}"
}

function test_search_posts_by_social_media() {
    sh ./social/bin/social scheduler create -p "2" -d "2090-10-02T09:00:00Z" -s "LINKEDIN" > /dev/null

    output=$(sh ./social/bin/social post -l --social-media "LINKEDIN")
    local expected="2. release notes"
    assert_contains "${expected}" "${output}"
}

#!/bin/bash

function set_up_before_script() {
  ./distribute.sh
}

function tear_down_after_script() {
  rm -rf ./data
  mkdir ./data
}

###############################################
## configuration
###############################################
function test_can_create_configuration() {
  local config=$(cat e2e/config.example.json)
  output=$(sh ./social/bin/social configuration -c "$config")
  local expected="Configuration has been stored"
  assert_contains "${expected}" "${output}"
}

function test_does_not_accept_empty_configuration() {
  local config=$(cat e2e/config.example.json)
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
function test_schedule_a_post() {
    output=$(sh ./social/bin/social scheduler create -p "1" -d "2090-10-02T09:00:00Z" -s "TWITTER")
    local expected="Post has been scheduled using UTC timezone"
    assert_contains "${expected}" "${output}"
}

function test_list_scheduled_post() {
    output=$(sh ./social/bin/social scheduler list)
    local expected="1. Post with id 1 will be published on 2090-10-02T09:00:00Z"
    assert_contains "${expected}" "${output}"
}

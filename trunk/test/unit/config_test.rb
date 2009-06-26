# config_test.rb

# -*- encoding: utf-8 -*-

require File.expand_path(File.dirname(__FILE__) + '/../test_helper')

class ConfigTest <  Test::Unit::TestCase
  def setup
    @config = JAVAWS::Configuration.new
  end
  def test_load
    assert_equal true, @config.yaml.nil?
    @config.load 'krobot.yml'
    assert_equal false, @config.yaml.nil?
  end
  def test_read
    @config.load 'krobot.yml'
    assert_equal false, @config.read('krobot').nil?
    assert_equal false, @config.read('krobot.username').nil?
  end
end

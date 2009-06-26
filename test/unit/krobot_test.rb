# krobot_test.rb

# -*- encoding: utf-8 -*-

require File.expand_path(File.dirname(__FILE__) + '/../test_helper')

class KRobotTest <  Test::Unit::TestCase
  def setup
    require 'logger'
    @robot = KRobot.new
    @robot.log     = Logger.new(File.dirname(__FILE__) + '/../../logs/krobot.log') if @robot.log.nil?
    @robot.browser = JAVAWS::Browser.new({\
      :log => @robot.log, 
      :config => @robot.config,
      :cookie => @robot.cache[:cookie]
    })
  end
  def test_desc
    assert_equal true, !@robot.desc.upcase.index('KROBOT').nil?
  end 
  def test_login_task
    assert_equal false, @robot.conds[:login?]
    @robot.do :login
    assert_equal true, @robot.conds[:login?]
  end
end

# vim: fileencoding=utf-8

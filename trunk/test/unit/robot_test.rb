# robot_test.rb

# -*- encoding: utf-8 -*-

require File.expand_path(File.dirname(__FILE__) + '/../test_helper')

class Robot
  include JAVAWS::BOT::ROBOT
end

class RobotTest <  Test::Unit::TestCase
  
  def setup
    require 'logger'
    @robot = Robot.new
    @robot.log = Logger.new(STDOUT)
  end 
  def test_robot_created
    assert_equal true, !@robot.nil?
  end
  def test_robot_log
    assert_equal true, !@robot.log.nil?
  end 
  def test_robot_desc
    assert_equal true, @robot.desc.length > 0
  end
end

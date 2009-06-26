#!/usr/bin/env ruby
# vim: ff=unix fileencoding=gb18030
# $Id: iwap.ora.dump.rb,v 1.5 2009/05/07 10:08:46 jhsea3do Exp $

$:.unshift File.join(File.dirname(__FILE__), '..', 'lib')

require 'rubygems'
require 'json'
require 'hpricot'
require 'logger'
require 'javaws'
require 'javaws/config'
require 'javaws/browser'
require 'javaws/bot'
require 'javaws/bot/trobot'

$KCODE = 'UTF8'

ftasks = ARGV[1] || "#{File.dirname(__FILE__)}/robot.yml"

robot = TRobot.new
robot.browser = JAVAWS::Browser.new({
  :config => robot.config, 
  :log => robot.log,
  :cookie => robot.cache[:cookie]
})

YAML::load(File.new(ftasks))['tasks'].each do |task, options|
  robot.do task, options 
end

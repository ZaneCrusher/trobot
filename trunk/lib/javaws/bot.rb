# bot.rb

# -*- encoding: utf-8 -*-

module JAVAWS::BOT
  # ....
end

module JAVAWS::LOG
  class Log < Logger
    def puts(m = nil)
      self << "#{m}\n"
    end
    def p(m = nil)
      self.puts m
    end
  end
end

module JAVAWS::BOT::ROBOT
  attr :id
  attr :log,    true
  attr :name,   true
  attr :conds,  true
  attr :config, true
  def desc
    return self.class.to_s
  end 
  def do(tsk = nil, options = {})
    yield(self, tsk, options) if block_given?
    task = nil
    if tsk.class == Symbol || tsk.class == String
      begin
        name = "#{tsk.to_s.sub(/^./, tsk.to_s[0,1].upcase)}Task"
        klass = eval("JAVAWS::BOT::TASK::#{name}")
        task = klass.new(self, options)
      rescue
        @log.p "! task not exists: #{tsk}, #{$!}"
        task = nil
      end
    end
    if !task.nil?
      @log.p "# task #{task.desc}"
      task.start
    end
  end
  def log=(log = nil)
    @log = log
    if @log.methods.index("p").nil?
      def @log.p(message = nil)
        self << "\n#{message}"
      end
    end
  end
end

# Robot Action
module JAVAWS::BOT::ACTION
  attr :robot, true
  attr :log, true
  attr :config, true
  attr :browser, true
  attr :cond, true
  attr :options, true
  def initialize(robot = nil, options = {})
    @robot = robot
    @options = options 
    @log = @robot.log if !@robot.nil?
    @config = @robot.config if !@robot.nil?
    @browser = @robot.browser if !@robot.nil?
    expr = "#{self.class}".split('::').last
    @cond = "#{expr.downcase.sub(/action$/, '')}?".to_sym
    # @log.p "! cond #{@cond}" if !@log.nil?
  end
  def desc
    return @cond || self.class.to_s
  end
  def before
    # @robot.log.p "# bact #{self.desc}" if !@robot.nil?
    yield(self) if block_given?
  end
  def on
    @robot.log.p "# oact #{self.desc}" if !@robot.nil?
    yield(self) if block_given?
  end
  def after
    # @robot.log.p "# aact #{self.desc}" if !@robot.nil?
    yield(self) if block_given?
  end

  # base action
  class BaseAction
    include JAVAWS::BOT::ACTION
  end

  # reset action
  class ResetAction < BaseAction
    def on
      @robot.log.p "! #{@robot} reset now!"
    end
  end
end

# Robot Task
module JAVAWS::BOT::TASK
  include JAVAWS::BOT::ACTION
  def run(acts = [])
    yield(self, acts) if block_given?
    acts.each do |act|
      action = nil
      if act.class == Symbol || atc.class == String
        begin
          name = "#{act.to_s.sub(/^./, act.to_s[0,1].upcase)}Action"
          klass = eval("JAVAWS::BOT::ACTION::#{name}")
          action = klass.new(@robot, self.options)
        rescue
          @log.p "! actn not exists: #{act}, #{$!}"
          action = nil
        end
      end
      if !action.nil?
        # @log.p "# actn #{action.desc}"
        action.before
        action.on
        action.after
      end
    end
  end
  def check(cond = nil)
    ret = false
    begin
      symb =  "#{cond}?".gsub(/\?\?/, '?').to_sym
      # @log.p "! cond #{symb}"
      ret = @robot.conds[symb]
    rescue
      ret = false
    end
    return ret
  end
  def start
    # implements 
  end
end

# Robot Behavior
module JAVAWS::BOT::BEHAVIOR
  # TODO implements
  attr :robot
  def bind(robot)
    @robot = robot
  end
  def desc
    return self.class.to_s
  end
  def action(name = :reset)
    return nil
  end
  def actions
    return [:reset]
  end
  def tasks
    return []
  end
end


# tasks.rb

module JAVAWS::BOT::TASK

class BaseTask
  include JAVAWS::BOT::TASK
  def desc
    return "#{@robot.desc} => #{@cond}"
  end

end

# login
class LoginTask < BaseTask
  def start
    3.times do
      run [:login, :home]
      break if check :login?
    end
  end 
end

# home
class HomeTask < BaseTask
end

# detect
class DetectTask < BaseTask
  def start
    1.times do
      run [:login, :home]
      break if check :login?
    end
  end
end

class ActivationTask < BaseTask
  PROMPT = "$"
  def help(cmd = nil)
    puts "# welcome to use #{@robot.class.to_s.downcase}:"
    @robot.actions.each do |actions|
      puts "# run: #{actions}:\t#{actions.to_s.upcase}"
    end
  end
  def start
    # run [:login, :home]
    options = nil
    while true
      STDOUT << "#{PROMPT} "
      cmd = gets
      next if cmd.nil?
      break if cmd.match(/^exit/)
      ( help; next; ) if cmd.match(/^help/)
      #begin
        params = cmd.strip.split(/\s+/)
        next if params.nil? || params.length < 1
        actions = params[0].split(",").collect{|v| [v.to_sym]}.flatten
        actions.each do |action|
          raise "action not allow" if @robot.actions.index(action).nil?
        end
        params.slice! 0
        params << '' if params.length % 2 == 1
        options = Hash[*params]
	options = Hash[*options.map{|k,v| [k.to_sym,v]}.flatten]
        opt = @options
        @options = options if !options.nil?
        run actions
        @options = opt
      #rescue
      #  @log.p "! cmd: #{cmd.strip} error: #{$!}"
      #end
    end
  end
end
end

# tasks.rb

module JAVAWS::BOT::TASK

class LoginTask
  include JAVAWS::BOT::TASK
  def desc
    return "#{@robot.desc} => login"
  end
  def start
    3.times do
      run [:login, :home]
      break if check :login?
    end
  end 
end

class SendTask
  include JAVAWS::BOT::TASK
  def desc
    return "#{@robot.desc} => send"
  end
  def start
    3.times do
      run [:login, :send]
      break if check :login?
    end
  end 
end

end

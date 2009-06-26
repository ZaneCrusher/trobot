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

class HouseFarmTask
  def run
    [:login, :farmShop].each do |action|
      @robot.do action
    end
  end
end

class HouseGardenTask
  include JAVAWS::BOT::TASK
  def desc
    return "#{@robot.desc} => login"
  end
  def start
    3.times do
      run [:login, :home, :gardenHome, :gardenShop]
      break if check :login?
    end
  end 
end

end

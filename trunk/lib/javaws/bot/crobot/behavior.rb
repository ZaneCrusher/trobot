# tasks.rb

class Behavior
  include JAVAWS::BOT::BEHAVIOR
  def actions
    return [:reset, :home, :login, :logout, :gardenShop]
  end
end

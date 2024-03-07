# MineTogether

The MineTogether mod is a Minecraft mod that integrates with Twitch-chat.
It allows for chat to help the streamer mine for materials in Minecraft

## Contribute

If you want to contribute to this project you can issue an pull-request or let us know your idea in [Twitch](https://twitch.tv/trysomethingdev). 

## How it works

### Load the mod

// TODO: explain how streamers can load the mod

### Craft a MineTogether Chest

Craft a MineTogether Chest using this recipe:

// TODO: make picture of recipe

Place the chest at the origin of your mine
This activates MineTogether automatically in Twitch chat
This message is displayed in the chat:
> MineTogether mode is active. Type "!mine <NumberOfBlocks>" in chat to help me mine.

The chatter is charged channel points for each block they want to mine.
After 60 seconds people can not use the command anymore. A message will be displayed that they cannot 
use the command anymore. Then *Twitch Chat Miners* will automatically start mining in game.

### Mining is finished
Once all the miners finish mining another message is dislayed in chat:
> MineTogether complete. Awarding Points...

Miners are rewarded depending on what resources they've collected. All materials are stored in the chest,
alongside a book with a list of what each chatter collected.

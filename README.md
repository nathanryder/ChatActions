# Description
Allows for the creation of different channels that are used in chat by customizable prefixes/suffixes as well as a a customizable range. Also includes a chat logging system that players can use to save their chat. Players can also use to change the chat color inside them to the color defined in the config
## Commands
- /chatactions reload - Reloads the plugin
- /chatactions spy - Shows all channels to the user
- /vedigiocata - View your saved logs, or if no arguments given it shows a GUI with all controls
- /salvagiocata - Starts logging the chat
- /stopgiocata - Stops logging the chat

## Permissions
- /chatactions reload - **chatactions.reload**
- /chatactions spy - **chatactions.spy**
- chatactions.arcane - Players without this permission cannot see messages from players who have it - replaces messages with &k - **chatactions.arcane.bypass** - Ignores if the player has chatactions.arcane - /vedigiocata - **stg.vedigiocata**
- /salvagiocata - **stg.salvagiocata**
- /stopgiocata - **stg.stopgiocata**


# Config

```
Messages:
  NoPermissions: "&cYou do not have the required permissions!"
  ConfigReloaded: "&aSuccessfully reloaded config!"
  InvalidArgs: "&cInvalid arguments! Correct usage: /chatactions "
  SpyEnabled: "&aSpy mode enabled!"
  SpyDisabled: "&cSpy mode disabled!"
  SpyFormat: "[%action%] %player%: %msg%"
  Cooldown: "&cPlease wait %time% more seconds before talking in %channel%!"

bracketColor: '5'

Channels:
  Global:
    format: "%player% &6>> &6%msg%"
    colorCode: '6'
    range: 0
    permission: "chatactions.channels.global"
    prefix: "!"
    suffix: ""
    cooldown: 2
  Normal:
    format: "%player% &6>>&2 %msg%"
    colorCode: '2'
    range: 10
    permission: "chatactions.channels.normal"
    cooldown: 0
  Whisper:
    format: "%player% &6>>&r %msg%"
    colorCode: 'r'
    prefix: "*"
    suffix: ""
    range: 3
    permission: "chatactions.channels.whisper"
    cooldown: 5
  Action:
    format: "%player% &8 %msg%"
    colorCode: '8'
    prefix: "+"
    suffix: ""
    range: 15
    permission: "chatactions.channels.whisper"
    cooldown: 5
  TheFate:
    format: "&3%msg%"
    colorCode: '3'
    prefix: "#"
    suffix: ""
    range: 20
    permission: "chatactions.channels.thefate"
    cooldown: 5
  Environment:
    format: "&9%msg%"
    colorCode: '9'
    prefix: "%"
    suffix: ""
    range: 20
    permission: "chatactions.channels.environment"
    cooldown: 5
  Yell:
    format: "%player% &6>>&c %msg%"
    colorCode: 'c'
    prefix: ""
    suffix: "!"
    range: 20
    permission: "chatactions.channels.whisper"
    cooldown: 5
  Scream:
    format: "%player% &6>>&4 %msg%"
    colorCode: '4'
    prefix: ""
    suffix: "!!"
    range: 30
    permission: "chatactions.channels.whisper"
    cooldown: 5

#SaveTheGame Settings
#Should the players display name or account name be displayed?
#true - display name
#false - account name
displayname: true

#Show the time beside each line?
displayTime: false

#Max amount of logs a player can have
#Keep it less than 9
amountOfLogs: 4```

# shared-timetable

####  A synchronized client-server to display ongoing & upcoming events on a/multiple display(s).
_Probably only compatible with Linux, untested on Windows_

## Capabilities
- MySQL database for storing events & users
- Discord bot to quickly add/edit/list events
- Multiple clients can connect simultaneously and are kept synchronized through a websocket connection
- Clients support multiple frame styles

## Config
### Server
**Directory:** `~/.config/shared-timetable-server/` 

- `db_connector.json`
  - `username`: The MySQL username
  - `password`: The MySQL password
  - `host`: The MySQL host
  - `port`: The MySQL port

- `discord_bot.json`
  - `token`: The discord bot's token

### Client
**Directory:** `~/.config/shared-timetable-client/`

- `frame.json`
  - `style`: `classic` or `train`, the style of the used frame. Can be overwritten at runtime with the `--frame.style=` cmd line arg.

- `remote.json`
  - `user.name`: The username used for authentication to the server
  - `user.pass`: The password used for authentication to the server
  - `user.token`: The token used for authentication to the server
  - `user.regenToken`: Whether the token should be regenerated using the username/password (iff the given token is invalid/empty)
  - `server.secure`: Use https & wss instead of http & ws
  - `user.url`: The server's url/ip (incl. port if needed. Default server port: `8443`, can be owerwritten using the `--server.port` cmd line arg.)

## Images
**Clients:** <br>

<img width="1920" height="1053" alt="image" src="https://github.com/user-attachments/assets/baa390ac-9e5b-4a24-b035-ffd1c195e79e" />
_Using frame: 'train'_ <br>

<img width="1920" height="1053" alt="image" src="https://github.com/user-attachments/assets/b3a37ee3-c607-41d4-b34f-042d380facc5" />

_Using frame: 'classic'_ <br>

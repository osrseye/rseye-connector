# rseye-connector

### Note: This plugin sends your username along with each request.

## Configuration

#### Endpoint Configuration
- `Endpoint` address which HTTP POST requests will be sent
- `bearerToken` used to verify identity of the sender

#### Data Toggles
- `Position Data` if player position data can be sent
- `Login Data` if player login state data can be sent (logged in/logged out)
- `Stat Data` if player stat data can be sent
- `Quest Data` if player quest data can be sent
- `Bank Data` if player bank data can be sent
- `Loot Data` if loot data from npc kills can be sent
- `Inventory Data` if player inventory data can be sent
- `Equipment Data` if player equipped item data can be sent
- `Death Data` if an event can be sent when a player dies
- `Overhead Data` if player overhead prayer data can be sent
- `Skull Data` if player skull data can be sent

#### Data Frequency
- `Position Data` how often in ticks position data can be sent
- `Overhead Data` how often in ticks overhead prayer data can be sent
- `Skull Data` how often in ticks skull data can be sent

## Endpoints
- `Position Data` <endpoint\> /position_update/
- `Login Data` <endpoint\> /login_update/
- `Stat Data` <endpoint\> /stat_update/
- `Quest Data` <endpoint\> /quest_update/
- `Bank Data` <endpoint\> /bank_update/
- `Loot Data` <endpoint\> /loot_update/
- `Inventory Data` <endpoint\> /inventory_update/
- `Equipment Data` <endpoint\> /equipment_update/
- `Death Data` <endpoint\> /death_update/
- `Overhead Data` <endpoint\> /overhead_update/
- `Skull Data` <endpoint\> /skull_update/

## POST Request Structure
### Header
- `Authorization Bearer: <token>` (all requests)
- `Request-Id: <string>` (all requests)

### Body

##### Position Update
###### Only fires if position data has changed since the last update.
```json
{
   "username":"cradcol1",
   "position":{
      "x":3183,
      "y":3436,
      "plane":0
   }
}
```

##### Login Update
```json
{
   "username":"cradcol1",
   "state":"LOGGED_IN"
}
```

##### Stat Update
###### When the user first logs in, an event will fire which contains all skill data, after which events will only fire upon `xp`, `level`, and `boostedLevel` changes, where losing/gaining hitpoints or prayer points count as a `boostedLevel` change. Furthermore, only the updated skills will be included in the request.
```json
{
   "username":"cradcol1",
   "combatLevel":86,
   "statChanges":[
      {
         "skill":"ATTACK",
         "xp":758722,
         "level":70,
         "boostedLevel":70
      },
      {
         "skill":"DEFENCE",
         "xp":753663,
         "level":70,
         "boostedLevel":70
      },
      {
         "skill":"STRENGTH",
         "xp":779001,
         "level":70,
         "boostedLevel":70
      }
     ...
   ]
}
```

##### Quest Update
###### When the user first logs in, an event will fire which contains all quest data, after which events will only fire on state change and will only include that/those specific quest change(s).
```json
{
   "username":"cradcol1",
   "questPoints":137,
   "questChanges":[
     {
       "id":359,
       "name":"Fairytale I - Growing Pains",
       "state":"FINISHED"
     },
     {
       "id":360,
       "name":"Fairytale II - Cure a Queen",
       "state":"IN_PROGRESS"
     },
     {
       "id":361,
       "name":"Family Crest",
       "state":"NOT_STARTED"
     }
     ...
   ]
 }
```

##### Bank Update
###### Once the player has opened and closed their bank an event will fire (each time) containing every item and its quantity. Placeholder items have different ID values to their non-placeholder counterparts and as such will still be of quantity 1.
```json
{
   "username":"cradcol1",
   "items":[
      {
         "id":995,
         "quantity":463519
      },
      {
         "id":2887,
         "quantity":1
      }
     ...
   ]
}
```

##### Loot Update
###### Upon killing an NPC or Player which drops loot, or if you receive chest loot from a raid or barrows. If no loot is rewarded this event will not fire.
```json
{
   "username":"cradcol1",
   "lootType":"NPC", // types: NPC, Player, Barrows, Chambers of Xeric, Theatre of Blood, Tombs of Amascut
   "entityId":3010,
   "entityName":"Man",
   "items":[
      {
         "id":526,
         "quantity":1
      },
      {
         "id":5318,
         "quantity":4
      }
   ]
}
```

##### Inventory Update
###### Preserves inventory spacing, for example if there is an item in the first slot and an item in the last slot with nothing between them, all other slots will be filled with items of `id: -1` and `quantity: 0`
```json
{
  "username":"cradcol1",
  "items":[
    {
      "id":995,
      "quantity":21014
    },
    {
      "id":772,
      "quantity":1
    },
    {
      "id":-1,
      "quantity":0
    }
    ...
  ]
}
```

##### Equipment Update
###### If the player isn't wearing an item in one of the slots that slot will be omitted from the update.
```json
{
   "username":"cradcol1",
   "items":{
      "GLOVES":{
         "id":11859,
         "quantity":1
      },
      "WEAPON":{
         "id":4587,
         "quantity":1
      },
      "SHIELD":{
         "id":12954,
         "quantity":1
      },
      "HEAD":{
         "id":11851,
         "quantity":1
      },
      "AMULET":{
         "id":3865,
         "quantity":1
      },
      "CAPE":{
         "id":13121,
         "quantity":1
      },
      "LEGS":{
         "id":11857,
         "quantity":1
      },
      "BODY":{
         "id":11855,
         "quantity":1
      },
      "RING":{
         "id":2550,
         "quantity":1
      },
      "BOOTS":{
         "id":11861,
         "quantity":1
      },
      "AMMO":{
         "id":9140,
         "quantity":5
      }
   }
}
```

##### Death Update
```json
{
  "username":"cradcol1"
}
```

##### Overhead Update
###### If the player stops using an overhead prayer, the `"overhead":` portion of the data will be omitted. Other possible values can be found on the official runelite api [documentation](https://static.runelite.net/api/runelite-api/net/runelite/api/HeadIcon.html).
```json
{
  "username":"cradcol1",
  "overhead":"MAGIC"
}
```

##### Skull Update
###### If the player stops being skulled, the `"skull":` portion of the data will be omitted. Other possible values can be found on the official runelite api [documentation](https://static.runelite.net/api/runelite-api/net/runelite/api/SkullIcon.html).
```json
{
  "username":"cradcol1",
  "skull":"0"
}
```

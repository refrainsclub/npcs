# NPCs

This is a plugin that enables you to create lightweight NPCs.

## Usage

1. Build the plugin using `gradle jar`
2. Install the plugin to your Spigot server.
3. Add the plugin as a dependency to your Spigot plugin.

## Plugin Goals

- The aim of this plugin is to provide a lightweght NPCs solution.
- This plugin will NOT support custom logic, it is up to you to implement.

## Example

```java
// Get the NPCs API
NpcsApi npcsApi = NpcsPlugin.getApi()

// Create a global NPC
Npc npc = npcsApi.createNpc("Test", location, true);

// Get a skin from Mojang API
Skin skin = SkinUtil.getSkin("Notch");

// Set the NPC's skin
npc.setSkin(skin);
```

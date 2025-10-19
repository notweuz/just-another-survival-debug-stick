# 🪄 Just Another Survival Debug Stick

**A lightweight server-side Fabric mod that enables survival players to craft and use the Debug Stick with high customization.**

---

## 📖 Overview

The **Just Another Survival Debug Stick** mod allows players in survival mode to craft and use the Debug Stick just like in Creative
mode.  
It brings back the full power of block state editing — now accessible without cheats or operator permissions.

This project is heavily inspired by [Just_S’s Survival Debug Stick (SDS)](https://modrinth.com/mod/survival-debug-stick)
mod but rewritten from scratch to be **simpler** (and probably more lightweight), and **up-to-date** (because Just_S's mod hasn't been updated for a while) with modern Minecraft versions. 

---

## 🔍  Showcase

By default, (when not configured) it works with every block and property. Blocks & properties can be easily customized with mod's config system.

Showcase of chest properties editing
![showcase.gif](https://raw.githubusercontent.com/notweuz/just-another-survival-debug-stick/preview/gifs/showcase-chest.gif)

Showcase of editing blacklisted `waterlogged` property and not whitelisted `black_bed` block.
How to configure whitelist & blacklist is located below, in «Configuration» category
![showcase.gif](https://raw.githubusercontent.com/notweuz/just-another-survival-debug-stick/preview/gifs/blacklist-showcase.gif)

---

## ⚙️ Configuration

Mod allows you to configure whitelist and blacklist for both blocks and properties, as well as debug stick usage and swapping cooldown.

Config contains: `config.json`, `whitelist.json` and `blacklist.json` located in `jasds` folder inside default config folder

Example `config.json`:
```json5
{
  "propertySwapCooldown": 20, // cooldown for swapping between different properties
  "useCooldown": 15, // cooldown for using debug stick on a block
  "whitelistEnabled": false, // enables blocks & properties whitelist
  "blacklistEnabled": false // enables blocks & properties blacklist
}
```
Example `whitelist.json` and `blacklist.json`:
```json5
{
  /*
    All blocks formats should look like registry:blockid. For example default minecraft dirt block will be "minecraft:dirt"
    Example non-empty blocks section
    "blocks": ["minecraft:oak_leaves", "minecraft:furnace"]
    
    Properties names you can get by clicking with debug stick on block. For example default minecraft furnace lit property will be "lit"
    Example non-empty block section
    "properties": ["lit", "waterlogged"]
  */
  "blocks": [],
  "properties": []
}
```

---

## ⌨️ Commands

`/jasds reload` – Reloads all mod configuration files (`config.json`, `whitelist.json`, `blacklist.json`).

`/jasds whitelist list/enable/disable` – Displays, enables, or disables the whitelist for blocks and properties.

`/jasds blacklist list/enable/disable` – Displays, enables, or disables the blacklist for blocks and properties.

`/jasds cooldowns use get/set` – Gets or sets the cooldown (in ticks) for using the debug stick.

`/jasds cooldowns swap get/set` – Gets or sets the cooldown (in ticks) for swapping block properties with the debug stick.

---

## 🛠 Crafting Recipe

Mod uses shapeless recipe provided below.

![crafting-grid.png](https://raw.githubusercontent.com/notweuz/just-another-survival-debug-stick/preview/images/default-craft.png)

You can easily edit craft by changing it in mod's compiled json or change it in source and compile it
```json
{
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
        "minecraft:amethyst_shard",
        "minecraft:netherite_scrap",
        "minecraft:diamond",
        "minecraft:stick"
    ],
    "result": {
        "id": "minecraft:debug_stick",
        "count": 1
    },
    "group": "stuff"
}
```

---

## ✅ Plans
- [x] Add whitelist & backlist customization (for properties & blocks)
- [ ] Backport to older Minecraft version (1.21.4 - 1.21.8)
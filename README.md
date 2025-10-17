# 🪄 Survival Debug Stick

**A lightweight Fabric mod that enables survival players to craft and use the Debug Stick — without any restrictions,
literally.**

---

## 📖 Overview

The **Survival Debug Stick** mod allows players in survival mode to craft and use the Debug Stick just like in Creative
mode.  
It brings back the full power of block state editing — now accessible without cheats or operator permissions.

This project is heavily inspired by [Just_S’s Survival Debug Stick (SDS)](https://modrinth.com/mod/survival-debug-stick)
mod but rewritten from scratch to be **lightweight**, and **up-to-date** with modern Minecraft versions.

---

## 🛠 Crafting Recipe

Mod uses shapeless recipe provided below.

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
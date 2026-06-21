# Machine Add Workflow Overview

Use this skill when adding or extending machine-style content in this workspace.

## When to use

Use this skill for any machine-style addition, including:

- machine blocks
- block entities
- GUI machines with Menu and Screen
- custom machine recipes with RecipeType and RecipeSerializer
- hopper and item pipe compatibility
- JEI recipe display integration

Cobblestone Furnace is the baseline example for machine implementation in this project. Start from the closest existing machine and only add what is missing.

## What to check first

Before changing code, confirm:

1. Whether the machine needs a GUI
2. Whether it has an internal inventory
3. Whether it uses a custom recipe
4. Whether it needs external automation support
5. Whether it needs JEI display support
6. Whether it has integer values that must sync, such as progress, burn time, or active state
7. Whether it matches the existing 1 input / 1 output Cobblestone Furnace shape or needs multiple slots

If these points are unclear, Menu, Screen, BlockEntity, and Recipe responsibilities tend to drift apart.

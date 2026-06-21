## Overview
This is a workspace for Minecraft mod development.

## Environment
- JAVA 21
- Minecraft 1.21.1
- Minecraft Neoforge 1.21.1

## Code Generation and Explanation Policy
- Treat the user as a Java beginner and prioritize clarity in implementations and suggestions.
- Avoid complex syntax and advanced patterns whenever possible; prefer straightforward control flow and clear variable names.
- Do not force the use of lambda expressions, the Stream API, complex generics, or tricky shorthand unless there is a clear benefit.
- When changing code, make it easy to follow why the change is needed and what will happen as a result.
- Respond in Japanese and explain things in an order that a beginner can follow.

## Comment Policy
- Write comments in Japanese.
- Explain the purpose, assumptions, and result of the code in enough detail that even basic behavior is clear.
- Do not omit comments too aggressively; write them so the purpose, assumptions, and result of the processing are easy to understand.
- For Minecraft- and NeoForge-specific mechanisms, event registration, data generation, and client-only processing, prioritize comments that clearly convey intent.
- For multi-step processing, add comments at each logical block as needed.

## Implementation Policy
- First follow the flow of the existing code, and do not break naming conventions or structure.
- Avoid unnecessary abstraction; split logic into small, easy-to-read methods.
- Do not change too much at once; prefer diffs that make cause and effect easy to trace.
- When an error occurs, make it clear which condition caused the failure.
- Prefer a structure with clear entry and exit points so beginners can follow the processing more easily.

## Points to Keep in Mind for NeoForge / Minecraft Mod Development
- Implement with Minecraft 1.21.1 and NeoForge 1.21.1 as the baseline.
- You may assume Java 21, but only use newer syntax when it actually improves readability.
- Do not mix client-only logic with logic that must also run on the server.
- Be aware of the differences between registration, event subscription, resource placement, and data generation output locations.
- Follow the existing Gradle configuration and assume generated resources will be placed under src/generated/resources.

## Verification Policy During Work
- After making changes, verify them with the smallest possible impact when feasible.
- The basic verification commands for this workspace are gradlew.bat build, gradlew.bat runClient, and gradlew.bat runData.
- If verification is expensive, prioritize compilation and consistency checks for the affected area first.
- For implementation details, check online sources when possible and prioritize matching the style and structure of the existing codebase.

# NLK Compatibility

A Paper plugin foundation for previewing selected newer Minecraft client features while keeping the server backend on an older Paper version.

## Target

- Paper 1.21.11
- Java 21
- Java clients newer than the server through ViaVersion
- Bedrock clients through Geyser/Floodgate
- Selected newer features implemented as compatibility modules

## Important

NLK is **not** a full Minecraft server-version conversion. It cannot make every 26.x engine/world feature exist on Paper 1.21.11.

The goal is to reproduce or backport features that can safely be implemented at the plugin/protocol/resource-pack level, while leaving the existing world and player data untouched.

## Roadmap

- [x] Project/build foundation
- [x] Paper 1.21.11 API target
- [x] GitHub Actions build
- [ ] 26.2 preview feature registry
- [ ] New item/block presentation layer
- [ ] Java client compatibility integration
- [ ] Geyser/Bedrock compatibility integration
- [ ] Feature toggles and per-world configuration
- [ ] 26.3 preview modules

## Commands

`/nlk status`

`/nlk reload`

## Build

GitHub Actions builds the plugin automatically on every push to `main`. The compiled JAR is published as a workflow artifact.

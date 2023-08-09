![Header](https://ref.zeith.org/images/rexouium/peek.webp)
# Minecraft Forge Mod Template 
This is a simple repository that serves as a template for creating a Minecraft 1.20+ mod.

# Prerequisites
- Intellij IDEA
- Installed Java 17 JDK
- If you want to publish your mod:
  - For uploading on Modrinth, assign `MODRINTH_TOKEN` environment variable to your [Modrinth authorization token](https://modrinth.com/settings/account)
  - For uploading on CurseForge, assign `CURSEFORGE_TOKEN` environment variable to your [CurseForge token](https://legacy.curseforge.com/account/api-tokens)
    - To open environment variable editor in Windows, Press `Win+R`, paste this command `rundll32 sysdm.cpl,EditEnvironmentVariables` and hit enter. 
- Any modifications with previous step require a full computer restart in order for the environment variables to be accessible to applications.

# Setup procedure
- Copy this repository to your computer either by:
  - Select the branch that you want to make the mod on, and download the repository as ZIP file;
  - Clone the repository and checkout the branch of version you are interested in.
- Open `build.gradle` with IntelliJ IDEA as a project.
- Wait for things to set up before proceeding.

# Things you need to *edit*
Before coding your awesome mod, you are going to need to fill out it's basic information.
This is done in `build.txt` file.

You will have to fill out `# Mod Info` category.
If you don't want to use HammerLib, simply remove `hl_version` and delete the mod dependency in `/src/main/resources/META-INF/mods.toml`.

Afterwards, fill out `cf_project` and `modrinth_project` (both are optional) with your project IDs (Modrinth is highyly recommended to allow for forge update checking)

# Things you need to *know*
`build_candidate` to `false` if you are building a public deploy.
When the variable is left at `false`, each build will increment the last piece of SemVer (`mod_version`) to +1, so you don't have to worry about it every time.
Otherwise, use `true` when you're building a dev build to test in a normal environment (like a modpack).

By default, the template project uses your Modrinth to perform forge version checking.
The homepage is also set to Modrinth by default.
If you ever wish to change this, do so in `/src/main/resources/META-INF/mods.toml`.
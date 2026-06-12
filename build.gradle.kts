plugins {
  id("me.roundaround.allay")
}

allay {
  displayName.set("More Stats")
  description.set("Track more stats in your Minecraft worlds.")
  authors.set(listOf("Roundaround"))
  license.set("MIT")
  homepage.set("https://modrinth.com/mod/more-stats")
  repository.set("https://github.com/Roundaround/mc-more-stats")
  issues.set("https://github.com/Roundaround/mc-more-stats/issues")
  logoFile.set("assets/morestats/banner.png")

  modrinth {
    projectId.set("more-stats")
  }

  curseforge {
    projectId.set(1572480)
  }

  release {
    versionType.set("release")
    minecraftVersions("26.1".."26.1.2")
    changelogDir.set(file("changelogs"))
  }
}

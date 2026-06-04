plugins {
  id("me.roundaround.allay")
}

allay {
  displayName.set("More Stats")
  description.set("Track more stats in your Minecraft worlds.")
  authors.set(listOf("Roundaround"))
  license.set("MIT")
  homepage.set("https://modrinth.com/mod/more-stats")
  repository.set("https://github.com/Roundaround/mc-fabric-more-stats")
  issues.set("https://github.com/Roundaround/mc-fabric-more-stats/issues")

  modrinth {
    projectId.set("more-stats")
  }

  release {
    versionType.set("release")
    sourcesJar.set(true)
  }
}

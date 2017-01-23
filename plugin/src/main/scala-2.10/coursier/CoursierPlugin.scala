package coursier

import sbt._
import sbt.Keys._

object CoursierPlugin extends AutoPlugin {

  override def trigger = allRequirements

  override def requires = sbt.plugins.IvyPlugin

  object autoImport {
    val coursierParallelDownloads = Keys.coursierParallelDownloads
    val coursierMaxIterations = Keys.coursierMaxIterations
    val coursierDefaultArtifactType = Keys.coursierDefaultArtifactType
    val coursierChecksums = Keys.coursierChecksums
    val coursierArtifactsChecksums = Keys.coursierArtifactsChecksums
    val coursierCachePolicies = Keys.coursierCachePolicies
    val coursierTtl = Keys.coursierTtl
    val coursierVerbosity = Keys.coursierVerbosity
    val mavenProfiles = Keys.mavenProfiles
    val coursierResolvers = Keys.coursierResolvers
    val coursierRecursiveResolvers = Keys.coursierRecursiveResolvers
    val coursierSbtResolvers = Keys.coursierSbtResolvers
    val coursierUseSbtCredentials = Keys.coursierUseSbtCredentials
    val coursierCredentials = Keys.coursierCredentials
    val coursierFallbackDependencies = Keys.coursierFallbackDependencies
    val coursierCache = Keys.coursierCache
    val coursierProject = Keys.coursierProject
    val coursierInterProjectDependencies = Keys.coursierInterProjectDependencies
    val coursierPublications = Keys.coursierPublications
    val coursierSbtClassifiersModule = Keys.coursierSbtClassifiersModule

    val coursierConfigurations = Keys.coursierConfigurations

    val coursierResolution = Keys.coursierResolution
    val coursierSbtClassifiersResolution = Keys.coursierSbtClassifiersResolution

    val coursierDependencyTree = Keys.coursierDependencyTree
    val coursierDependencyInverseTree = Keys.coursierDependencyInverseTree

    val coursierArtifacts = Keys.coursierArtifacts
  }

  import autoImport._

  lazy val treeSettings = Seq(
    coursierDependencyTree <<= Tasks.coursierDependencyTreeTask(
      inverse = false
    ),
    coursierDependencyInverseTree <<= Tasks.coursierDependencyTreeTask(
      inverse = true
    )
  )

  def coursierSettings(
    shadedConfigOpt: Option[(String, String)],
    packageConfigs: Seq[(Configuration, String)]
  ) = Seq(
    coursierParallelDownloads := 6,
    coursierMaxIterations := 50,
    coursierDefaultArtifactType := "",
    coursierChecksums := Seq(Some("SHA-1"), None),
    coursierArtifactsChecksums := Seq(None),
    coursierCachePolicies := CachePolicy.default,
    coursierTtl := Cache.defaultTtl,
    coursierVerbosity := Settings.defaultVerbosityLevel,
    mavenProfiles := Set.empty,
    coursierResolvers <<= Tasks.coursierResolversTask,
    coursierRecursiveResolvers <<= Tasks.coursierRecursiveResolversTask,
    coursierSbtResolvers <<= externalResolvers in updateSbtClassifiers,
    coursierUseSbtCredentials := false,
    coursierCredentials := Map.empty,
    coursierFallbackDependencies <<= Tasks.coursierFallbackDependenciesTask,
    coursierCache := Cache.default,
    coursierArtifacts <<= Tasks.artifactFilesOrErrors(withClassifiers = false),
    update <<= Tasks.updateTask(
      shadedConfigOpt,
      withClassifiers = false
    ),
    updateClassifiers <<= Tasks.updateTask(
      shadedConfigOpt,
      withClassifiers = true,
      ignoreArtifactErrors = true
    ),
    updateSbtClassifiers in Defaults.TaskGlobal <<= Tasks.updateTask(
      shadedConfigOpt,
      withClassifiers = true,
      sbtClassifiers = true,
      ignoreArtifactErrors = true
    ),
    coursierProject <<= Tasks.coursierProjectTask,
    coursierInterProjectDependencies <<= Tasks.coursierInterProjectDependenciesTask,
    coursierPublications <<= Tasks.coursierPublicationsTask(packageConfigs: _*),
    coursierSbtClassifiersModule <<= classifiersModule in updateSbtClassifiers,
    coursierConfigurations <<= Tasks.coursierConfigurationsTask(None),
    coursierResolution <<= Tasks.resolutionTask(),
    coursierSbtClassifiersResolution <<= Tasks.resolutionTask(
      sbtClassifiers = true
    )
  )

  override lazy val projectSettings = coursierSettings(None, Seq(Compile, Test).map(c => c -> c.name)) ++
    inConfig(Compile)(treeSettings) ++
    inConfig(Test)(treeSettings)

}
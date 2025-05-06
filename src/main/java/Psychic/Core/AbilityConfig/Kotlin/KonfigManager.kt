package Psychic.Core.AbilityConfig.Kotlin

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

class KonfigManager(pluginInstance: Plugin) {
    companion object {
        private var plugin: Plugin? = null
        private val abilityInstances = mutableMapOf<Class<*>, Any>()

        fun loadConfig(instance: Any) {
            val clazz = instance::class.java
            val nameAnnotation = clazz.getAnnotation(Kame::class.java)
                ?: return

            abilityInstances[clazz] = instance // 클래스와 인스턴스 매핑 저장
            reloadConfig(instance)
        }

        fun reloadConfig(instance: Any) {
            val clazz = instance::class.java
            val nameAnnotation = clazz.getAnnotation(Kame::class.java)
                ?: return

            val abilityName = nameAnnotation.value
            val abilitiesFolder = File(plugin?.dataFolder, "abilities")
            val configFile = File(abilitiesFolder, "$abilityName.yml")

            if (!configFile.exists()) {
                saveDefaultConfig(instance)
                return
            }

            val config = YamlConfiguration.loadConfiguration(configFile)

            clazz.declaredFields.forEach { field ->
                if (field.isAnnotationPresent(Konfig::class.java)) {
                    field.isAccessible = true
                    val path = field.name

                    if (config.contains(path)) {
                        val value = config.get(path)
                        try {
                            when {
                                field.type == Material::class.java && value is String -> {
                                    field.set(instance, Material.valueOf(value))
                                }
                                field.type == Int::class.java && value is Number -> {
                                    field.set(instance, value.toInt())
                                }
                                field.type == Double::class.java && value is Number -> {
                                    field.set(instance, value.toDouble())
                                }
                                else -> field.set(instance, value)
                            }
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: IllegalArgumentException) {
                            plugin?.logger?.warning("Invalid material name in config for $abilityName: $value")
                        }
                    }
                }
            }
        }

        private fun saveDefaultConfig(instance: Any) {
            val clazz = instance::class.java
            val nameAnnotation = clazz.getAnnotation(Kame::class.java)
                ?: return

            val abilitiesFolder = File(plugin?.dataFolder, "abilities")
            if (!abilitiesFolder.exists()) {
                abilitiesFolder.mkdirs()
            }

            val configFile = File(abilitiesFolder, "${nameAnnotation.value}.yml")
            val config = YamlConfiguration.loadConfiguration(configFile)

            clazz.declaredFields.forEach { field ->
                if (field.isAnnotationPresent(Konfig::class.java)) {
                    field.isAccessible = true
                    val path = field.name
                    try {
                        val value = field.get(instance)
                        when (value) {
                            is Material -> config.set(path, value.toString())
                            else -> config.set(path, value)
                        }
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }
            }

            try {
                config.save(configFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun reloadAllConfigs() {
            abilityInstances.values.forEach { instance ->
                reloadConfig(instance)
            }
        }
    }

    init {
        plugin = pluginInstance
    }
}
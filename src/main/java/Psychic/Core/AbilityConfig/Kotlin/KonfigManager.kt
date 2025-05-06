package Psychic.Core.AbilityConfig.Kotlin

import Psychic.Core.AbilityConfig.Java.Config
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File


class KonfigManager(plugin: Plugin?) {
    init {
        Companion.plugin = plugin
    }

    companion object {
        private var plugin: Plugin? = null
        private val abilityInstances: MutableMap<Class<*>?, Any> = HashMap<Class<*>?, Any>()

        fun loadConfig(instance: Any) {
            val clazz: Class<*> = instance.javaClass
            val nameAnnotation = clazz.getAnnotation<Kame?>(Kame::class.java)

            if (nameAnnotation == null) return

            abilityInstances.put(clazz, instance) // 클래스와 인스턴스 매핑 저장
            reloadConfig(instance)
        }

        fun reloadConfig(instance: Any) {
            val clazz: Class<*> = instance.javaClass
            val nameAnnotation = clazz.getAnnotation<Kame?>(Kame::class.java)
            if (nameAnnotation == null) return

            val abilityName: String? = nameAnnotation.value
            val abilitiesFolder = File(plugin?.getDataFolder(), "abilities")
            val configFile = File(abilitiesFolder, abilityName + ".yml")

            if (!configFile.exists()) {
                saveDefaultConfig(instance)
                return
            }

            val config: FileConfiguration = YamlConfiguration.loadConfiguration(configFile)

            for (field in clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Config::class.java)) {
                    field.setAccessible(true)
                    val path = field.getName()

                    if (config.contains(path)) {
                        try {
                            val value = config.get(path)
                            // 필드 타입에 맞게 변환
                            if (field.getType() == Int::class.javaPrimitiveType && value is Number) {
                                field.set(instance, value.toInt())
                            } else if (field.getType() == Double::class.javaPrimitiveType && value is Number) {
                                field.set(instance, value.toDouble())
                            } else {
                                field.set(instance, value)
                            }
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        private fun saveDefaultConfig(instance: Any) {
            val clazz: Class<*> = instance.javaClass
            val nameAnnotation = clazz.getAnnotation<Kame?>(Kame::class.java)

            val abilitiesFolder = File(plugin?.getDataFolder(), "abilities")
            if (!abilitiesFolder.exists()) {
                abilitiesFolder.mkdirs()
            }

            val configFile = File(abilitiesFolder, nameAnnotation!!.value + ".yml")
            val config: FileConfiguration = YamlConfiguration.loadConfiguration(configFile)

            for (field in clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Konfig::class.java)) {
                    field.setAccessible(true)
                    val path = field.getName()
                    try {
                        config.set(path, field.get(instance))
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

        // 모든 능력의 설정을 다시 로드하는 메서드
        fun reloadAllConfigs() {
            for (instance in KonfigManager.Companion.abilityInstances.values) {
                KonfigManager.Companion.reloadConfig(instance)
            }
        }
    }
}
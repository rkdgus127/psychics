package Psychic.Core.AbilityConfig.Java

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.reflect.Modifier

class KotlinConfigProcessor(private val plugin: JavaPlugin) {
    fun process(clazz: Class<*>) {
        try {
            var fileName: String? = null
            for (field in clazz.declaredFields) {
                if (!field.isAnnotationPresent(Config::class.java)) continue
                if (!Modifier.isStatic(field.modifiers)) continue

                field.isAccessible = true

                // 파일 이름 설정
                if (field.name.equals("Name", ignoreCase = true) && field.type == String::class.java) {
                    fileName = field[null] as String
                    break
                }
            }

            if (fileName == null) {
                plugin.logger.warning(clazz.simpleName + "에 @Config Name 필드가 없음!")
                return
            }

            // config 파일
            val file = File(plugin.dataFolder, "$fileName.yml")
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }

            val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

            // 필드들을 config에 저장/로딩
            for (field in clazz.declaredFields) {
                if (!field.isAnnotationPresent(Config::class.java)) continue
                if (!Modifier.isStatic(field.modifiers)) continue

                field.isAccessible = true
                val key = field.name
                val defaultValue = field[null]

                if (!config.contains(key)) {
                    config[key] = defaultValue
                } else {
                    val value = config[key]
                    field[null] = value
                }
            }

            config.save(file)
        } catch (e: Exception) {
            plugin.logger.severe("Config 처리 실패: " + e.message)
            e.printStackTrace()
        }
    }
}
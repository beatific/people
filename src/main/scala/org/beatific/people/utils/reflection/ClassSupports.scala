package org.beatific.people.utils.reflection

import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.io.Resource
import org.springframework.util.ClassUtils
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.`type`.classreading.MetadataReader
import org.springframework.core.`type`.classreading.CachingMetadataReaderFactory
import org.springframework.core.`type`.classreading.MetadataReaderFactory
import java.util.Collection
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object ClassSupports {

  val DEFAULT_RESOURCE_PATTERN: String = "**/*.class"
  val PATH_SEPARATOR: String = "/"
  val PACKAGE_SEPARATOR = "."
  
  val resourcePatternResolver: ResourcePatternResolver = new PathMatchingResourcePatternResolver()
  val metadataReaderFactory: MetadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver)

  private def resolveBasePackage(basePackage: String): String = {
    return ClassUtils.convertClassNameToResourcePath(basePackage);
  }

  private def getClass(metadataReader: MetadataReader): Class[_] = {
    ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), ClassUtils.getDefaultClassLoader());
  }

  private def findAllClass(basePackages: Array[String]): Array[Class[_]] = {
    basePackages.flatMap {
      basePackage =>
      val packageSearchPath: String = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(basePackage) + PATH_SEPARATOR + DEFAULT_RESOURCE_PATTERN;
      val resources: Array[Resource] = resourcePatternResolver.getResources(packageSearchPath)
      resources filter (_.isReadable()) map (resource => getClass(metadataReaderFactory.getMetadataReader(resource)))
    }
  }
  
  def findClassName(basePackage: String, name: String): Option[Class[_]] = {
    name match {
      case name if name.contains(PACKAGE_SEPARATOR) => findClassByAbsoluteName(Array(basePackage), name)
      case name => findClassByShortcutName(Array(basePackage), name)
    }
  }
  
  def findClassName(basePackages: Array[String], name: String): Option[Class[_]] = {
    name match {
      case name if name.contains(PACKAGE_SEPARATOR) => findClassByAbsoluteName(basePackages, name)
      case name => findClassByShortcutName(basePackages, name)
    }
  }

  private def findClassByShortcutName(basePackages: Array[String], suffix: String): Option[Class[_]] = {
    findClassByFiltering(basePackages)(clazz => clazz.getName.split(PACKAGE_SEPARATOR).last == suffix)
  }

  private def findClassByAbsoluteName(basePackages: Array[String], name: String): Option[Class[_]] = {
    findClassByFiltering(basePackages)(clazz => clazz.getName == name)
  }

  private def findClassByFiltering(basePackages: Array[String])(filter: Class[_] => Boolean): Option[Class[_]] = {
    
    lazy val classes = findAllClass(basePackages)
    val clazz = classes.filter(filter)
    clazz match {
      case clazz if clazz.length == 1 => Some(clazz(0))
      case clazz if clazz.length == 0 => None
      case clazz                      => None
    }
  }
  
  private def findClassesByFiltering(basePackages: Array[String])(filter: Class[_] => Boolean): Array[Class[_]] = {
    
    lazy val classes = findAllClass(basePackages)
    classes.filter(filter)
  }
  
  def findClassBySuperClass(basePackages: Array[String], superclass: Class[_]): Option[Class[_]] = {
    
    findClassByFiltering(basePackages)(clazz => clazz.isInstance(superclass))
  }
  
  def findClassBySuperClass[T](basePackage: String, superclass: Class[T]): Array[Class[T]] = {
    
    findClassesByFiltering(Array(basePackage))(clazz => clazz.isInstance(superclass)).map(clazz => clazz.asInstanceOf[Class[T]])
  }
  
  def genericType[T](clazz :Class[_], index:Int) :Class[T] = {
     clazz.getGenericSuperclass.asInstanceOf[ParameterizedType].getActualTypeArguments()(index).asInstanceOf[Class[T]]
  }
  
}
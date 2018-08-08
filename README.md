# docker-compose 部署应用
使用 docker 运行应用时，需要加一堆参数很繁琐，而且更新应用时有可能会因为忘了加某些参数而导致问题。好在 docker 官方提供了[docker-compose](https://docs.docker.com/compose/overview/)来方便我们部署应用。

基本用法看[docker-compose官方文档](https://docs.docker.com/compose/overview/)，compose 文件写法查看[docker-compose手册](https://docs.docker.com/compose/compose-file/)。

## 此项目使用说明
为了方便镜像的构建和提交，在[build.gradle](build.gradle)中添加了 gradle 的 docker 插件[palantir](https://github.com/palantir/gradle-docker)：
```groovy
id 'com.palantir.docker' version '0.19.2'
```

该插件提供了 5 个 task：
- docker
- dockerClean
- dockerfileZip
- dockerPrepare
- dockerPush

常用的只有`docker`和`dockerPush`这两个 task：

### docker task
运行 docker task 之前，需要先运行 build 或者 bootJar task 生成可执行 jar 包。
```groovy
/* build.gradle */
ext {
    dockerRegistry = 'registry.cn-hangzhou.aliyuncs.com' // 镜像仓库地址
    dockerNamespace = 'ecxpp' // 镜像命名空间
}

docker {
    name = "$dockerRegistry/$dockerNamespace/$rootProject.name:$version"
    copySpec.from(bootJar.archivePath)
            .rename(bootJar.archiveName, 'app.jar') // 将`gradle bootJar`生成的可执行 jar 包拷贝到 docker 的构建目录
}
```
在上面的配置中，配置了 docker task 打包的参数，包括镜像的`仓库地址/命名空间/镜像名称(使用项目名)/版本号(使用项目版本号)`。

运行`gradle docker`时，插件会
- 在`build/`目录下创建一个名为**docker**的文件夹，将项目目录下的 Dockerfile 拷贝进这个目录，同时还会执行 build.gradle 配置中的 docker task 块中的逻辑，在这个例子中则是将可执行 jar 包拷贝到`build/docker/`下并命名为`app.jar`。
- 然后在`build/docker/`目录下开始构建 docker 镜像，在这个例子中等同于执行`docker build -t registry.cn-hangzhou.aliyuncs.com/lvxixiao/docker-demo:<当前版本号> .`。

task 执行完成后，在终端运行`docker images`即可看到刚刚构建的镜像。

### dockerPush task
这个运行后就会将刚刚构建好的镜像推送到镜像仓库，不过要注意首先要在本机上登录镜像仓库`docker login <镜像仓库地址>`。

### 使用`docker-compose`部署
将目录下的[compose.yml](compose.yml)拷贝到任意安装了 docker 和 docker-compose 的主机上。

运行`docker-compose -f compose.yml up`即可启动项目。

如果要后台运行，只需要在上面命令后加上`-d`。

如果镜像更新了版本，只需要修改 compose.yml 文件中 image 的版本号，然后重新运行上述命令即可，比如：

~~image: registry.cn-hangzhou.aliyuncs.com/lvxixiao/docker-demo:0.0.2~~

image: registry.cn-hangzhou.aliyuncs.com/lvxixiao/docker-demo:0.0.3


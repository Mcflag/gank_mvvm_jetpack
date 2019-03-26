## 一、技术选择Android Jetpack

选择使用Android Jetpack中的一些功能，以及接入其他的三方库。


### 1 Foundation基础功能

包括核心功能组件，提供向后兼容性，Kotlin扩展，对multidex的支持，以及自动化测试。

#### 1.1 AppCompat

使用AppCompat主要是为了用到MaterialDesign以及相应的ToolBar，并且在低版本的安卓手机上适当降级。

虽然我们的应用界面设计上没有用到android自己的ToolBar，不过还是可以继承自AppCompat，毕竟它提供了更好的兼容性。

#### 1.2 Android KTX

Android KTX是用Kotlin实现的一组Android工具库，可以让代码更加简洁。作为工具库实际上并不是一定要使用，但是从使代码简洁的角度来说，使用Android KTX更好。

特别是对于listener事件的写法，实现代码的代码结构要缩进几层，但是用Android KTX封装的方法，只用只用实现一个方法，写的更简洁。

Android KTX简化和封装代码的方式基本上是使用Kotlin的Extensions，用lambda简化，以及使用默认参数。

#### 1.3 Multidex分包方案

分包是为了解决方法数超过64K崩溃的问题，目前在已经在API 21（Android 5.0）中提供了通用的解决方案，那就是android-support-multidex.jar。这个jar包最低可以支持到API 4的版本(Android L及以上版本会默认支持mutidex)。

打包时，把一个应用分成多个dex，例：classes.dex、classes2.dex、classes3.dex…，加载的时候把这些dex都追加到DexPathList对应的数组中，这样就解决了方法数的限制。

Andorid 5.0之后，ART虚拟机天然支持MultiDex。

Andorid 5.0之前，系统只加载一个主dex，其它的dex采用MultiDex手段来加载。

目前大部分手机都是4.4以上，但是并不是所有的手机都是5.0以上的，所以minSdkVersion选择19。不过如果想考虑更多的设备，需要支持到4.0的话，minSdkVersion就要选择15。

不论选择支持19还是15，multidex都是需要接入的。

#### 1.4 Test自动化测试

主要是用于单元测试和运行时界面测试的Android测试框架。

不过自动化测试需要写测试代码，如果是比较完整的测试代码代码量还比较多，因此要看具体的工期决定测试代码的多少。如果时间不足，只对核心功能编写测试代码。

单元测试其实比较简单，就是针对功能代码编写一些测试用例，运行测试代码判断结果是否正确。要注意的就是测试的方法必须独立测试，方法间不能有依赖。使用单元测试一个是可以保证程序的正确，另一个方面可以保证代码功能的分离，只有不耦合的代码才能方便的写出单元测试。

Android测试使用Espresso和UiAutomator，通过模拟界面操作进行界面及Android相关功能的测试。

Espresso的功能强大适合应用中的功能性 UI 测试，但是有一个重要的局限，就是只能在被测试app的Context中操作。

所以应用的推送、从另一个应用程序进入、处理通知栏消息等跨系统和已安装应用的跨应用功能性UI测试需要混合UIAutomator测试。

UIAutomator要求的minSdkVersion是18，这个需要注意一下。

### 2 Architecture架构

可以设计出稳健、可测试且易维护的应用，主要是控制UI组件的生命周期和数据的持久化。

#### 2.1 Data Binding数据绑定

Data Binding指将数据绑定到UI元素上，更多的在xml进行数据绑定的操作，而不是在代码中处理。

Data Binding可以绑定数据源，操作，方法引用，监听器等。但是这种数据都是一次单向的绑定，假如绑定之后再修改数据源，不会产生UI的改变。

因此需要使用Observable objects，observable fields，observable collections来实现可实时刷新的单向或双向绑定。

使用双向绑定会让代码的复杂度提升一级，然而在某些情况下使用双向绑定又确实简单很多，比如EditText或者CheckBox的情况。

使用的时候遇到的一个坑：xml中“@{}”里面的“<”、“>”、“&”、“"”、“空格”号不能直接写，要用转义“&lt;”、“&gt;”、“&amp;”、“&quot;”、“&nbsp;”。

遇到的另一个坑：xml里慎用自动格式化。data binding根据xml的代码顺序执行，比如针对EditText，android:text="@={inputText}"和android:selection="@{inputText.length()}"两个语句，语句的顺序不能改变。但是自动格式化按照字典序，会修改两个语句的顺序，就会出现异常。

另外BR文件有时候会生成不出来，首先查看xml文件是否正确，xml中的data binding写法是否正确，如果均正确，再通过clean和rebuild重新构建，如果还是不行只有通过反射的方法获取BR类。

最佳实践：xml就是用来显示数据，需要计算的，判断的，通通在代码里先完成。

#### 2.2 Lifecycles

生命周期感知组件，用来处理生命周期的相关的操作。

Lifecycles是一个持有组件生命周期状态（如活动或片段）信息的类，并允许其他对象观察此状态。生命周期使用两个主要枚举来跟踪其关联组件的生命周期状态：

> Event：从框架和Lifecycle类派发的生命周期事件。 这些事件映射到活动和片段中的回调事件。
> State：由Lifecycle对象跟踪的组件的当前状态。

使用时就是相当于注册一个观察者LifecycleObserver，传给LifecycleOwner，由观察者处理遇到各个Event事件的时候该进行什么操作。

#### 2.3 LiveData

LiveData是一个可被观察的数据持有者类。与常规的Observable不同，LiveData能意识到应用程序组件的生命周期变化，这意味着它能遵守Activity、Fragment、Service等组件的生命周期。这种意识确保LiveData只更新处于活跃状态的应用程序组件Observer。

LiveData的创建基本会在ViewModel中，从而使数据在界面销毁时继续保持。

MutableLiveData是LiveData的扩展，重写了LiveData中的抽象方法，postValue()和setValue()中也只是调用了super.postValue()和super.setValue(),也就是说所有的方法都是在LiveData中实现。

LiveData整个作用过程就是两部分，一是使用LifeCycleOwner感知声明周期的变化，再一个就是储存并遍历Observer，在数据改变时回调所有的观察者。

不过问题在于LiveData的作用跟RxJava的作用有些重叠，RxJava有着丰富的支持以及太多的使用经验，而LiveData还不足够。

然而考虑到DataBinding提供了对LiveData的支持，Room和Paging同样提供了支持，以及有着安全的数据更新。RxJava在子线程进行UI的更新依赖于observerOn(AndroidSchedudler.mainThread())，但是LiveData不需要，只用通过postValue()，就能安全的进行数据更新。

不过我们可以同时拥有LiveData和RxJava，只需要将LiveData的observe()变成RxJava的Flowable。示例代码：

	private val autoLogin: MutableLiveData<Boolean> = MutableLiveData()
	autoLogin.toFlowable()   // 变成了一个Flowable
			.filter { it }
				.doOnNext { login() }
			.bindLifecycle(this)
			.subscribe()

toFlowable()是对LiveData的扩展，得益于kotlin的扩展函数。
				
	fun <T> LiveData<T>.toFlowable(): Flowable<T> = Flowable.create({ emitter ->
		val observer = Observer<T> { data ->
			data?.let { emitter.onNext(it) }
		}
		observeForever(observer)
		emitter.setCancellable {
			object : MainThreadDisposable() {
				override fun onDispose() = removeObserver(observer)
			}
		}
	}, BackpressureStrategy.LATEST)

#### 2.4 Navigation导航

Navigation用来处理应用内的导航。

它简化了Android应用程序中导航的实现，通过在xml中添加元素并指定导航的起始和目的地，在Fragment等组件之间建立连接，在Activity中调用xml中设置的导航action从而跳转界面到达目的地。

虽然Navigation多数用于Fragment中，但是它还支持Activity，导航图和子图，自定义目标。

不过Navigation从使用的感觉上是一种对单Activity多Fragment的支持库，虽然Navigation支持BottomNavigationView和ToolBar，但是Navigation内部对Fragment的切换采用的是replace(),这意味着，每次点击底部导航控件，都会销毁当前的Fragment，并且实例化一个新的Fragment。

在这个App3.0中要使用Navigation的话，只在根布局的页面通过Navigation导航。另外让我比较犹豫的一点就是他的配置在res中，是一个项目级的库，框架比较死对需求的变化不好快速响应。总而言之，就是Navigation可以用，但没必要。

#### 2.5 Paging分页

Paging库可以逐步从数据源按需加载信息。实际使用中，Paging可以使开发者更轻松在RecyclerView中分页加载数据，Paging正是以无限滚动的分页模式而设计的库。

Paging有三个部分：
1. DataSource： 数据源，数据的改变会驱动列表的更新，因此，数据源是很重要的
2. PageList：分页策略，它从数据源取出数据，同时，它负责控制第一次默认加载多少数据，之后每一次加载多少数据，如何加载等等，并将数据的变更反映到UI上。
3. PagedListAdapter: 适配器，RecyclerView的适配器，通过分析数据是否发生了改变，负责处理UI展示的逻辑（增加/删除/替换等）。

Paging的核心原理是将多种数据源，分页策略进行组合，交给适配器，然后委托给代理类的AsyncPagedListDiffer的submitList()方法处理，通知其内部保存的PageList更新数据，并刷新UI。

Paging的缺点很明显，那就是Paging本身是对RecyclerView.Adapter的继承，这意味着使用了Paging，就必须抛弃其他的Adapter库，或者自己改造，如果仅仅为了无线滚动分页就使用Paging的话，感觉有点得不偿失。

对于Adapter还可以使用DslAdapter，实现复杂的RecyclerView。因为没有一个好的库能够同时解决下拉刷新、无限滚动分页、复杂RecyclerView的显示这多种需求，需要在实际开发时根据具体情况再取舍使用。

#### 2.6 Room

Room是Google开发的一个ROM库，用于数据库的相关操作，支持RxJava和LiveData。它提供三个组件：

1. @Database：@Database用来注解类，并且注解的类必须是继承自RoomDatabase的抽象类。该类主要作用是创建数据库和创建Daos（data access objects，数据访问对象）。
2. @Entity：@Entity用来注解实体类，@Database通过entities属性引用被@Entity注解的类，并利用该类的所有字段作为表的列名来创建表。
3. @Dao：@Dao用来注解一个接口或者抽象方法，该类的作用是提供访问数据库的方法。在使用@Database注解的类中必须定一个不带参数的方法，这个方法返回使用@Dao注解的类。

Room使用比较简单，并且可以与LiveData和RxJava无缝衔接。

#### 2.7 ViewModel

ViewModel能够检测到持有者的生命周期，保存UI相关的数据，并避免了横竖屏切换时额外的代码的配置。

它的内部是通过一个不可见的Fragment对数据进行持有，并在真正该销毁数据的时候去销毁它们。

同时，它是MVVM中的核心组件，layout中所有的属性配置都应该依赖于ViewModel中的MutableLiveData属性。

ViewModel的一个优点是解决异步回调。通常我们app需要频繁异步请求数据，比如调接口请求服务器数据。这些请求的回调都是相当耗时，之前我们可以在Activity或fragment里接收这些回调，所以不得不考虑潜在的内存泄漏情况。比如Activity被销毁后接口请求才返回，处理这些问题比较麻烦。但现在利用ViewModel处理数据回调，可以完美的解决此问题。

另一个优点就是可以在Activity及Fragments间共享数据，比如在一个Activity里有多个fragment，fragment之间需要做某些交互。之前的做法是接口回调，需要统一在Activity里管理，并且不可避免的fragment之间还得互相持有对方的引用。这样做耦合度高，还需要大量的容错判断，比如对方的fragment是否还活着。使用ViewModel就可以解决这个问题，解耦Activity及Fragments。

由于ViewModel生命周期可能长于activity生命周期，所以为了避免内存泄漏Google禁止在ViewModel中引用view、LifeCycle或任何可能包含对Activity Context引用的类。

不过有一个继承自ViewModel的类AndroidViewModel，内部维护了一个ApplicationContext，实在要用Context可以用着个。

#### 2.8 WorkManager后台任务管理

WorkManger是提供执行后台任务管理的组件，适用于即使应用程序退出也会运行的任务，WorkManager的API可以方便的指定可延迟的异步任务以及何时运行它们。

WorkManager根据设备API级别和应用程序状态等因素选择适当的方式来运行任务。如果WorkManager在应用程序运行时执行任务，WorkManager可以在应用程序进程的新线程中运行任务。如果应用程序未运行，WorkManager会选择一种合适的方式来安排后台任务，具体取决于设备API级别和包含的依赖项，WorkManager可能会使用 JobScheduler，Firebase JobDispatcher或AlarmManager。

虽然看起来WorkManager的作用可以直接用Service实现，但是在Android8.0后，Google收紧了Service的权限。如果想启动service，必须实现服务前台化，调用Context.startForegroundService()在前台开启新服务。

系统创建服务，应用有五秒的时间来调用该服务的startForeground()方法以显示新服务的用户可见通知。如果应用在此时间限制内未调用startForeground()，则系统将停止服务并声明此应用为ANR。

所以，可以看出来service的使用范围会越来越小，取而代之的就是WorkManager。

### 3 UI界面

提供组件及辅助功能，使应用有更好的易用性。

#### 3.1 Animations & Transitions动画和过渡

动画能够给与操作反馈，以及使界面显得更易用，因此动画效果是必加且必须仔细设计实现的部分。动画可以分为属性动画，补间动画，帧动画以及界面过渡效果，动画主要看UI和用户体验如何设计。

动画效果的实现一般四个方向，android自带的动画；播放视频；使用gif图；使用OpenGL。

因为动画的性能要求和样式要求并不是像游戏那么高，所以没考虑使用OpenGL。而gif图动画效果达不到要求，所以不考虑。

帧动画和视图动画使用起来比较简单，但是对于复杂的组合动画效果不太好，并且在大屏幕上图片分辨率必须比较大才不会出现锯齿，如果使用帧动画，会有大量的图片资源。同时加载图片和加载动画既卡顿还容易出现OOM。

所以对于小的动画效果，使用视图动画和帧动画，对于大的复杂的动画效果可以使用播放视频的方式。

#### 3.2 Auto,TV & Wear便携设备及多屏幕支持

如果要支持其他设备，那么界面和操作都需要重新设计。现在应该主要着眼于手机，主要工作是适配各个分辨率的手机屏幕。

对于适配屏幕来说，参考了几种适配的方案，最好使用sw<N>dp限定符，即smallestWidth（最小宽度）限定符来进行适配。

在Android Studio中安装ScreenMatch插件，自动生成各个宽度下的dimens文件，选择其中一个最小宽度作为基准，在里面填好设计图的dp尺寸，通过插件自动生成其他最小宽度的dimens。在界面中直接使用相应的标注尺寸即可。

两个问题：一是这样做只是保证界面会等比缩放，对于不同宽高比例的设备来说，会有变形拉伸，设计时需要考虑一下长一些的或者宽一些的屏幕上的显示效果。二是对于横屏时、平板、TV、便携、车载设备来说，屏幕宽高比例变化太大，不是通过一套尺寸就能适配的，最好是需要美术重新出图，重新定一套尺寸。
 
#### 3.3 Emoji表情 

Android系统对Emoji有支持，但是某些Emoji会显示不出来，而且某些样式是Android自带的Emoji，效果不好。因此可以使用EmojiCompat支持库。
	
EmojiCompat库有两个问题，一个是最低支持到Android 4.4(Api Level 19)的系统设备。再一个就是EmojiCompat只是 补齐了当前设备不支持的那些Emoji表情，但是并没有将Android自带的果冻表情替换为标准的Emoji表情。

对于第一个问题，EmojiCompat对于低版本的系统是兼容的，显示上不会有问题。对于第二个问题，可以使用assets下捆绑的字体包把安卓自带的Emoji表情替换成新的字体显示。

但是如果想要支持EmojiCompat，需要下载字体包或者初始就将字体包放在assets资源文件夹中。字体文件有7M多，会增加APK的大小，并且加载Emoji字体，会增加200KB内存。

如果切实需要支持Emoji效果再考虑添加支持，否则不用接入。

#### 3.4 Fragment

Fragment就是碎片化，它在应用中可以降低内存，快速切换，增加用户流畅的体验。

Fragment使用起来有一些麻烦的地方，内存泄漏，内部状态异常，没有context，有一个attach到Activity的过程，生命周期依附于Activity的周期。此外，接收intent，接收Activity返回，设置title、option menu等等都麻烦一点。

为了解决Fragment的劣势，一般可以写一个专门的Activity用于装载任何一个fragment，功能覆盖上面谈及的缺点。

关于何时使用Fragment或者何时使用Activity，从使用的经验上来看fragment适合平行页面较多的应用，使用fragment可以增强使用者的流畅体验；而页面之间有先后关联，业务逻辑都是后继和返回的关系，用fragment不是最好反而是activity更好些。

从产品设计上来说，并且如果一个模块超重，那么就直接放Activity里面。如果一个模块比较碎片化，可能会今天放这明天挪那，那么放进一个fragment里面。

从业务角度上说，如果一个页面中有某些业务UI上不耦合，从逻辑上也不耦合，同样可以拆分成多个Fragment。比如直播的页面，直播的推拉流是一个单独的业务，上层的弹幕礼物是另一个单独的业务，可以拆分成两个Fragment，如果放在同一个Activity中，代码就太重了。

从安全的角度上来说，Activity容易被劫持，而Fragment不会。对于Activity劫持的解决办法是在onPause方式中弹出警示提示。可以在onKeyDown中识别是否由于用户按返回或者Home键造成的，如果不是那么就说明是被劫持后界面进入的onPause，需要弹出警示提示。

另外有一些大牛提出因为Fragment会出现大量的Bug，同时我们只需要View创建响应式UI，实现回退栈及屏幕事件的处理，不用Fragment也能满足实际开发的需求。不过这种思路稍微有些激进，考虑到我们这个项目的功能逻辑，也没有必要与Fragment断得如此干脆。

#### 3.5 Layout布局

现在一般声明布局的方式是用xml文件，可以更好地将应用的外观与控制应用行为的代码隔离。同时使用适配方案的时候也更加方便。

使用了DataBinding之后，布局文件中多了数据源的定义，xml布局文件又有了与数据源绑定的作用，这使得界面显示更加方便，布局文件还有了一些逻辑效果，比如控制部件的显示隐藏可以直接在xml中实现，再也不用在代码中啰嗦一大块代码。

#### 3.6 Palette调色盘

用Palette能获取到Bitmap中一些活跃的颜色，其他控件通过设置这些颜色来优化界面色彩搭配。

主要的使用方式就是传入一个bitmap，获取其中的活跃颜色，在回调中使用这些活跃颜色即可。将获取到的活跃颜色设置给其他的控件，这样界面上的控件和图片色调能够协调。

这个功能主要看UI是否有需求，如果有需求再接入。


### 4 Behavior行为

与标准Android服务（如通知、权限、分享）相集成。

#### 4.1 Download Manager下载更新

Android自带的下载方法，但是使用中容易出现问题。遇到过如果网址重定向，即不可下载的情况。

#### 4.2 Media & Playback视频及音频播放

App3.0中目前还没有功能用到视频播放。但是如果应用中增加动画的话，实际上简单的动画用android自带的动画可以实现，但是复杂的动画用Animation比较困难，可以使用MediaPlayer播放视频实现。

另外应用中用到了语音播报，是手动控制音频播放，在App3.0里，语音播报还是用播放音频文件的方式实现。

#### 4.3 Permissions权限管理

Android6.0之后需要动态的获取权限，可以使用谷歌官方的EasyPermissions。使用简单，并且有已经兼容androidx的版本。

#### 4.4 Notifications通知

通知是在应用的常规 UI 外部向用户显示的消息。当告知系统发出通知时，它将先以图标的形式显示在通知区域中。用户可以打开抽屉式通知栏查看通知的详细信息。 通知区域和抽屉式通知栏均是由系统控制的区域，用户可以随时查看。

现在使用的通知有交易通知，活动通知等。

#### 4.5 Sharing分享

虽然Android有自带的分享功能，但是对于每一家的分享来说都需要重新调整，比较麻烦。因此如果要实现分享功能，还是需要接入ShareSDK。

#### 4.6 Slices

Slice其实是一个UI展示模块，它可以在搜索APP、语音助手、关键字识别等动作中动态地显示你的APP部分模块的内容，能够让应用的某些功能从外部直接进入。

可以把这个Slices想成一个捷径，让使用者减少操作动作，让应用和操作更紧密结合。

具体是否需要使用这个功能，要看App3.0的具体设计，是否需要从搜索APP、语音助手、关键字识别等地方进入。


### 5 Third party

可以使用的一些第三方控件，简化开发。

#### 5.1 Glide图片加载库

常用的图片加载库，使用简单。不过现在App使用的时候有一个问题，就是图片加载过了一次之后就有缓存了。如果图片地址保持不变，即使图片的内容变了，加载的时候还是会显示原来缓存的图片，而不会重新加载。

这个问题的解决办法一个是用加signature的方式，判断图片是否变化。另外就是现在App里广告图的方式，通过接口读图片地址，每次换图的时候地址就需要改变。

#### 5.2 Kotlin Coroutines协程

使用简单的代码处理后台任务且不需要回调。它不是异步问题的唯一方法，但是在一些情况下使用可以让代码更简单。比如异步加载视频或者图片，这是就可以通过协程来加载，而不用直接在UI线程中处理，导致卡顿或者崩溃。

目前Room，网络请求以及Databinding依赖的LiveData，都是通过RxJava串在一起的，这些代码糅合程度很深。但是因为Kotlin Coroutines在kotlin 1.3中才从experimental转为release。协程还比较新，支持库不多，可能还没办法全面替代RxJava。

如果想要尝试的话，对于一些异步加载的动作，如读写文件，存储照片或者数据等操作都可以使用Kotlin Coroutines来完成。

但是为了稳妥一点，网络加载的部分还是使用Retrofit2加OkHttp3来完成。

#### 5.3 OkHttp3 & Retrofit2

常用的联网库，主要是考虑一下跟LiveData结合起来使用。

#### 5.4 链式编程和异步：RxJava2

也是常用的库用来处理异步操作，主要也是需要考虑跟LiveData和ViewModel结合起来使用。

#### 5.5 依赖注入：Dagger2或Kodein

Dagger2是常用的依赖注入库，主要复杂的地方在于每个Activity都有一个Scope（作用域）时，每个屏幕都必须实现一个Scope，一个Module和一个Component。

Dagger2中没有容器的概念。依赖关系通过构造函数，属性或方法注入。Controller不知道dataSource的来源。

但是在Kodein中使用容器来传递依赖关系，创建了一个容器，然后将容器传递给Controller。这之后，将检索依赖项的工作委托给了Controller。

Kodein是原生kotlin的库，使用比较简洁不需要大量的模板代码，只是因为比较新，可能一方面库及使用方法还不够成熟，再一个就是资料不多。因为他们互有优缺点，如果想保守一点的话选择Dagger，激进一点的话就选择Kodein。

#### 5.6 Gson 

常用的json转换库，主要是用于json的序列化和反序列化。

#### 5.7 EventBus

用于组件之间通信的库。可以解耦事件发送方和接收方，避免依赖和生命周期的问题。使用起来也很简单，发布事件，订阅事件即可。

不过EventBus有一些局限，就是一是对于一个事件的接受者和发送者没有统一的管理，分散在项目的各个地方，如果需要修改，容易遗漏。二是事件的通信是单向的，没有回调。

但是因为EventBus比广播更轻量，更解耦，所以简单的通知可以使用EventBus。

#### 5.8 DslAdapter

这个库是继承RecyclerView.Adapter的库，DslAdapter的设计思路是，将整个列表像搭积木一样组合起来，哪怕它再复杂。对于复杂的列表而言，需要面对的就是对不同类型数据的添加，然后将不同类型的数据展示在不同的ItemLayout上。

它同时还支持DataBinding，支持数据驱动UI的更新和自动更新。


## 二、其他功能点


### 1 后台保活

从现有的实现来看，后台保活没有百分之百成功的策略。在不同设备上，或者用户应用不同的设置，都可能导致后台保活的失效。

不过现有的策略有好几种，需要配合使用，并且还需要实际看效果。

#### 1.1 双进程绑定相互唤起

该方案适用于Android5.0以下，具体实现原理如下：

1. 创建两个service，分别注册在不同的进程中
2. 以启动方式绑定service时，A服务用B服务中的ServiceConnection实例，B服务用A服务的ServiceConnection实例。
3. 在ServiceConnection的onServiceDisconnected方法，去启动另一个服务。如A服务中的conn去启动B服务。

#### 1.2 JobScheduler保活

该方案使用与Android5.0，兼容7.0。但是如果主动多次结束进程后，便无法唤起。

使用方法重新开启一个JobService，在这个Service里面启动双进程的服务。

#### 1.3 WorkManager保活

WorkManager本身就是管理后台服务的组件，使用方法是在work中启动JobService以及双进程服务。

#### 1.4 无声背景音乐保活

该方法仅限用户点击清理全部应用时，不被结束。

#### 1.5 其他保活方案

1. 1像素Activity
基本思想，系统一般是不会杀死前台进程的。所以要使得进程常驻，我们只需要在锁屏的时候在本进程开启一个Activity，为了欺骗用户，让这个Activity的大小是1像素，并且透明无切换动画，在开屏幕的时候，把这个Activity关闭掉，所以这个就需要监听系统锁屏广播

2. 前台service
对于 API level < 18 ：调用startForeground(ID， new Notification())，发送空的Notification ，图标则不会显示。
对于 API level >= 18：在需要提优先级的service A启动一个InnerService，两个服务同时startForeground，且绑定同样的 ID。Stop 掉InnerService ，这样通知栏图标即被移除。

3. 粘性服务&与系统服务捆绑
这个是系统自带的，onStartCommand方法必须具有一个整形的返回值，这个整形的返回值用来告诉系统在服务启动完毕后，如果被Kill，系统将如何操作。
系统服务捆绑更厉害一点，比如NotificationListenerService就是一个监听通知的服务，只要手机收到了通知，NotificationListenerService都能监听到，即时用户把进程杀死，也能重启，所以说可以把这个服务放到我们的进程之中。
这种方案在某些情况or某些定制ROM上可能失效，我认为可以多做一种保保守方案。

4. 账号同步唤醒
创建一个账号并设置同步器，创建周期同步，系统会自动调用同步器，这样就能激活我们的APP，局限是国产机会修改最短同步周期（魅蓝NOTE2长达30分钟），并且需要联网才能使用。

5. 接入各个大厂自己的推送
比如接入小米推送接入，华为推送等等，通过ROM的推送来保证消息推送及时。

### 2 消息推送

仍然使用阿里云的推送。

### 3 数据分析

仍然使用阿里云的数据分析。

### 4 崩溃日志

仍然使用腾讯的Bugly。

### 5 热修复

仍然使用阿里云的热修复。

### 6 自动更新

获得更新信息的话可以通过接口获取。下载安装包既可以使用Download Manager，也可以使用第三方库。

可以用来下载更新的有update-app这个第三方库。使用方式是：

	implementation 'com.qianwen:update-app:3.5.2'
    implementation('com.qianwen:okhttp-utils:3.8.0') {
        exclude module: "fastjson"
    }
    implementation 'com.lzy.net:okgo:3.0.4'
	
对于这个第三方库，自带的更新弹窗有几个默认，网易云音乐的更新弹窗跟其中一个一样，可能网易云音乐用的也是这个自动更新的库。


## 三、项目代码组织结构

### 1 MVVM分层设计

因为业务的需求变得复杂，显示什么和做什么，也就是界面显示和业务逻辑越来越复杂，相互之间耦合太紧的话将会带来难以开发难以维护的问题。为了实现功能，UI和逻辑必须一起工作，这里包含一个哲学问题，UI和逻辑看起来在一起，但是同时又不在一起。为了将其分开，针对项目的结构提出了分层的思想。

从MVC的分层设计开始，基本的设计思路是View触发事件 ->  Controller处理了业务，然后触发了数据更新 -> 不知道谁更新了Model的数据 -> Model（带着数据）回到了View -> View更新数据。

MVC把用户的需求分成了三个部分。界面视图及推动业务的事件（包含用户对界面的操作）放在View中、业务放在了Controller中、数据分到了Model层由它携带。

但是假如需求复杂，Controller会变得复杂，一个变动可能需要同时维护多个交互和多个对象，MVC的分层方法就变得比较复杂，分层的效果不明显。

于是出现了针对MVC优化后的MVP分层。MVP中的Presenter实际是Controller的升级，MVP方式切断了Model和View的连接，Model只与Presenter产生关联。而在View和Presenter之间加了一个接口层View Interface。

加这个接口层的主要目的是可以首先定义好界面的操作，并根据已有的接口协议去分别独立开发操作产生的业务功能，以此去分治解决界面需求变化频繁的问题。

对于前端来说，view层靠近用户，有极大的可能是需求变化最频繁的地方。不过如果对于后端来说，View层不是UI而是API的话，这个假设就不太成立。

MVVM分层设计可以认为是MVP的进化，它将Presenter改为了ViewModel，并减少了视图接口。

它是直接交互，用数据“绑定”的形式让数据更新的事件不需要开发人员手动去编写特殊用例，而是自动地双向同步。数据绑定可以认为是Observer模式或者是Publish/Subscribe模式，原理都是为了用一种统一的集中的方式实现频繁需要被实现的数据更新问题。

### 2 状态管理

* 什么是状态？界面上展示给用户的都是状态，如loading状态，error状态，列表展示状态等等。
* 为什么要状态管理？一个View界面可以把它抽象成是各个状态的集合，通过单独处理各个状态能让逻辑分离，提高代码的可读性可维护性。

比如下面这个在自助收银中遇到的例子：购物车里仅有的一个商品需要删除。删除商品->显示loading->请求网络（这是个异步操作）->成功的话隐藏loading，支付按钮变为不可点击->出错时隐藏loading并显示error信息，支付按钮仍然可点击。

会在不同的地方调用showLoading()，dismissLoading()，showError()，dismissError()等方法，但是往往就容易遗漏。万一再加上界面的显隐控制，或者其他的配套方法，很容易出现遗漏或者状态混乱。

基于这种问题，有人提出了MVI的模式。I指的是Intent，这个不是Android的Intent，而是从View层输入的操作意图，MVI就是一种基于用户的操作/命令/动作来驱动的模型。其中的Model被提高了重要性，一个Model对应的就是一个状态。

三个部分的整体思路就是view(model(intent()))，将用户操作（如触摸，点击，滑动等）作为数据流的输入，传递给model()方法。model()方法把intent()方法的输出作为输入来创建Model（状态），传递给view()。view()方法把model()方法的输出的Model（状态）作为输入，根据Model（状态）的结果来展示界面。

实际上MVI并不能够算是一个模式，它的提出者是和MVP结合起来用的，不过在MVVM中一样可以使用，重点就在View和ViewModel中做变化。

1. View中暴露两个方法：
	
> fun intents(): Observable<I> //将用户意图传递给ViewModel
> fun render(state: S) //订阅ViewModel输出的状态用于展示界面

2. ViewModel中同样暴露两个方法:

> fun processIntents(intents: Observable<I>) //处理View传递过来的用户意图
> fun states(): Observable<S> //输出状态给View，用于渲染界面

3. 数据流

> 1. View层产生的操作传入给intents()方法，由其将多个intent传递给ViewModel层。
> 2. ViewModel层processIntents()方法接收所有的intent然后拆分，传递给不同的处理方法process()。
> 3. ViewModel层的处理方法与Model层交互处理得到result。
> 4. ViewModel层的方法接收result并生成state，然后用Reducer组合多个state传递给View层。
> 5. View层的render()方法接收全部的state并按照各个状态显示出来。

### 3 mvvm基本结构

#### 3.1 Model层

IRepository负责调度数据的获取，它的下面分为两个接口，IRemoteDataSource负责联网访问数据，ILocalDataSource负责读取本地数据。

项目中使用Room获取和储存本地序列化的数据，使用Retrofit+OkHttp获取联网数据。不过本地数据并不一定是从数据库中得到，还可以从文件和SharedPreference中得到，只需要实现ILocalDataSource接口即可。

数据获取到了之后，不论是Room还是Retrofit+OkHttp+RxJava都可以转为LiveData包含的数据源，非常方便。

#### 3.2 ViewModel层

ViewModel作为数据的存储和驱动。同时要兼顾数据源的操作，View的状态处理两个方面。


#### 3.3 View层

Activity和Fragment负责产品与用户的交互。


### 4 模块

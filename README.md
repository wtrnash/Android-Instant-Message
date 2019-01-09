# Android-Instant-Message
基于asmack,Openfire的安卓即时通讯app
功能大致分为三个模块：用户管理模块，匹配好友模块以及即时通讯模块。
其中用户管理模块包含登录、注册、信息资料维护三个子模块；
匹配好友模块包含搜索好友、匹配好友两个子模块；
即时通讯模块包含文字聊天、图片发送、语音发送以及群聊四个子模块。

文字聊天：  
主界面选择MessageFragment，用ListView来存储聊天的信息。在登录后添加聊天监听。
利用asmack的API sendMessage来发送信息。当发送信息时，
将信息设成 “发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长”的格式，
发送消息后更新对话框以及存放对话的ListView,在消息显示的碎片中进行显示。接收信息时则对消息进行拆分，
然后根据信息的属性，更新对应ListView及碎片。

图片发送：
首先点击按钮，使用intent来调用相机或相册，得到bitmap后，将bitmap通过base64转为string，然后通过volley 框架，用post将参数传到服务器。服务器获取到post后，得到参数，并用base64解码写入文件。即完成将图片上传到服务器，然后response返回图片在服务器上的URL。客户端获得URL后，将message用类似发送文字一样的方式发送，只不过内容是URL,发送到接收方。接收方获得到URL后开个子线程，在子线程中用image loader框架将url加载成bitmap，赋给imageView，完成图像的接收。

语音发送：
语音发送和图像发送的流程比较类似，首先调用麦克风进行音频录制，录制完后用base64转为String类型，用Volley post发送到服务器，服务器再解码转为3gp类型存在服务器本地，将URL返回。客户端接收到URL后用类似发送文字的方式，内容为该URL，发给接收方，接收方收到后，设置message中的语音URL为该URL，点击播放用MediaPlayer，传入对应URL即可完成语音的播放。


下表为用户表，其他信息表等即时通讯相关的表都通过openfire服务器实现。

|序号|属性名|	别名|	数据类型|	数据长度|	完整性约束码|	描述|
| ------ | ------ | ------ | ------ | ------ | ------ | ------ |
|1|	用户名|	User Name|	varchar|	20|	主码|	用户的唯一标识。|
|2|	用户密码|	Password|	varchar|	20||		用户登录页面的唯一标识。|
|3|	姓名|	Name|	varchar|	10||		用户的真实姓名。|
|4|	性别|	Sex|	varchar|	2||	男或女|	用户的性别。|
|5|	年龄|	Age|	int|	2	||	用户的年龄。|
|6|	血型|	Blood type|	varchar|	2||		用户的血型。|
|7|	职业|	Occupation|	varchar|	20||		用户的职业。|
|8|	学历|	Education|	varchar|	20||		用户的最高学历。|
|9|	星座|	Constellation|	varchar|	10||		用户的星座。|
|10|	籍贯|	Native place|	varchar|	20||		用户的籍贯。|
|11|	爱好|	Hobby|	varchar|	100||		用户的爱好。|
|12|	邮箱|	Email|	varchar|	30||		用户的邮箱。|
|13|	联系方式|	Contact way|	int|	11||		用户的联系方式。|
|14|	所在地区|	Location|	varchar|	100||		用户长期所在的地区。|
|15|	个性签名|	Signature| 	text|||			用户对于交友类型的描述。|
|16|	头像|	Head image|	varchar|	30||		存放用户头像所在的URL地址|

# Replace Cursor

Replace mouse cursor with a custom one.

自定义包括鼠标指针、触控点在内的各种图片资源。

Note: You can use Magisk + [RRO](https://source.android.com/docs/core/runtime/rros) for better experience.
See `magisk` folder for more information.
(be aware of SELinux context, btw.)

![Demo picture, showing UI and example cursor](https://github.com/Young-Lord/replaceCursor/assets/51789698/416d64cc-f065-4b55-953a-6766c27e9f6a)

## How to use / 用法

> Tested on:  Android 10 (AOSP), Android 12 (MIUI 13)

1. Select `System framework` (package name may be `android` or `system` or empty, [see this](https://github.com/LSPosed/LSPosed/releases/tag/v1.9.1)) in module scope and activate the module
2. Force stop module
3. Add resources to change. Please make sure that image sizes are bigger than hotspot (cursor left-top corner / touch point), otherwise nothing will show.
4. Reboot (you MUST reboot when you modify anything, or changes will not be applied until next reboot)
5. Reverse engineer `/system/framework/framework-res.apk` to find out the resource ID of the cursor you want to replace.

For MiPad users, install [MaxMiPad](https://github.com/Xposed-Modules-Repo/com.yifeplayte.maxmipadinput/releases/latest) and enable `No Magic Pointer`.

If you have Windows-style `.ani` cursors, you can use [ani2png](https://github.com/Mastermindzh/Scripts/blob/master/c%2B%2B/ani2png.c) to convert them.
For example, use `ls -a *.ani | xargs -L1 ani2png` to convert all `.ani` files in current directory to `.png` files.

## Common resources / 常用资源

From MIUI 13, Android 12.

| Resource ID        | Description                                                                     | HotSpot  |
|--------------------|---------------------------------------------------------------------------------|----------|
| pointer_spot_touch | Touch point                                                                     | (22, 22) |
| pointer_arrow      | Mouse Pointer (Arrow)                                                           | (5, 5)   |
| pointer_hand       | Mouse Pointer (Hand, for example when hover on sth. clickable)                  | (9, 4)   |
| pointer_text       | Mouse Pointer (Looks like <code>&#124;</code>， for editing vertical text input) | (12, 12) |

Please note that the images may be scaled. See [Android 加载 drawable 中图片后自动缩放的原理](https://juejin.cn/post/6844903914022633480) and [Android drawable微技巧，你所不知道的drawable的那些细节](https://blog.csdn.net/guolin_blog/article/details/50727753#:~:text=%E5%8F%AF%E4%BB%A5%E7%9C%8B%E5%88%B0%EF%BC%8C%E6%AF%8F%E4%B8%80%E7%A7%8D%E5%AF%86%E5%BA%A6%E7%9A%84dpi%E8%8C%83%E5%9B%B4%E9%83%BD%E6%9C%89%E4%B8%80%E4%B8%AA%E6%9C%80%E5%A4%A7%E5%80%BC%EF%BC%8C%E8%BF%99%E4%B8%AA%E6%9C%80%E5%A4%A7%E5%80%BC%E4%B9%8B%E9%97%B4%E7%9A%84%E6%AF%94%E4%BE%8B%E5%B0%B1%E6%98%AF%E5%9B%BE%E7%89%87%E4%BC%9A%E8%A2%AB%E7%B3%BB%E7%BB%9F%E8%87%AA%E5%8A%A8%E6%94%BE%E5%A4%A7%E7%9A%84%E6%AF%94%E4%BE%8B%E3%80%82) and [getResources().getDisplayMetrics().density 的理解](https://blog.csdn.net/lgzaaron/article/details/52517941) for more informaton. In my case, `360x360` on a `xhdpi` device becomes `135x135` (`360 * (240 / 320) / 2 = 135`, not sure). You may need to manually adjust size & padding (border) to fit size & hotspot.

Mouse-related resource-id may have a `_large` suffix, used when `Accessibility` -> `Large mouse pointer`(`大号鼠标指针`) is enabled.

## Module Scope

- android

## Project URL

Home URL: <https://github.com/Young-Lord/replaceCursor>

Xposed Modules Repo URL: <https://github.com/Xposed-Modules-Repo/moe.lyniko.replacecursor>

## License

Apache-2.0 License or MIT License are all OK.

## Thanks

- <https://github.com/thesandipv/pointer_replacer> (doesn't work for me)
- <https://github.com/hujiayucc/R-Pointer> (per-app configuration)
- <https://github.com/quantum5/win2xcur> (convert `.cur` and `.ani` to Xcursor format, for Linux)
- <https://github.com/eworm-de/xcur2png> (convert Xcursor format to `.png`, see pull requests for a better version)

## TODO

Currently, this module use SharedPreferences to store images, which is really shitty. Anyone is free to improve this module.

Also, I am too low to modify hotspot using Xposed. If you know how to do this, please tell me.

Also: Origin image resolution, disable scaling.

# Replace Cursor

Replace mouse cursor with a custom one.

自定义包括鼠标指针、触控点在内的各种图片资源。

Note: You can use Magisk + [RRO](https://source.android.com/docs/core/runtime/rros) for better experience.
See `magisk` folder for more information.
(be aware of SELinux context, btw.)

## How to use / 用法

> Tested on: None

1. Select `System framework` (package name may be `android` or `system` or empty, [see this](https://github.com/LSPosed/LSPosed/releases/tag/v1.9.1)) in module scope and activate the module
2. Force stop module
3. Add resources to change. Please make sure that image sizes are bigger than hotspot (cursor left-top corner / touch point), otherwise nothing will show.
4. Reboot (you MUST reboot when you modify anything, or changes will not be applied until next reboot)
5. Reverse engineer `/system/framework/framework-res.apk` to find out the resource ID of the cursor you want to replace.

For MiPad users, install [MaxMiPad](https://github.com/Xposed-Modules-Repo/com.yifeplayte.maxmipadinput/releases/latest) and enable `No Magic Pointer`.

## Common resources / 常用资源

From MIUI 13, Android 12.

| Resource ID        | Desciption                                                     | HotSpot  |
|--------------------|----------------------------------------------------------------|----------|
| pointer_spot_touch | Touch point                                                    | (22, 22) |
| pointer_arrow      | Mouse Pointer (Arrow)                                          | (5, 5)   |
| pointer_hand       | Mouse Pointer (Hand, for example when hover on sth. clickable) | (9, 4)   |

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

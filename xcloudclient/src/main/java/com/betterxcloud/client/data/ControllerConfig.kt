package com.betterxcloud.client.data

import androidx.annotation.StringRes
import com.betterxcloud.client.R

/**
 * Enumeration describing the available controller kernels. The native kernel uses Android's
 * game controller APIs directly while the web kernel routes input through the Better XCloud
 * JavaScript shim. The user can toggle between them in the Options screen.
 */
enum class ControllerKernel(val id: String, @StringRes val label: Int, val description: Int) {
    Native("native", R.string.kernel_native, R.string.kernel_native_desc),
    Web("web", R.string.kernel_web, R.string.kernel_web_desc);

    companion object {
        fun fromId(id: String?): ControllerKernel = entries.firstOrNull { it.id == id } ?: Native
    }
}

/**
 * Enumerates the buttons on an Xbox-style controller that can be remapped. Each entry contains a
 * stable identifier that maps to the JavaScript configuration consumed by the Better XCloud script.
 */
enum class ControllerButton(val scriptKey: String, val displayName: Int) {
    A("buttonA", R.string.button_a),
    B("buttonB", R.string.button_b),
    X("buttonX", R.string.button_x),
    Y("buttonY", R.string.button_y),
    LeftBumper("buttonLB", R.string.button_lb),
    RightBumper("buttonRB", R.string.button_rb),
    LeftTrigger("buttonLT", R.string.button_lt),
    RightTrigger("buttonRT", R.string.button_rt),
    View("buttonView", R.string.button_view),
    Menu("buttonMenu", R.string.button_menu),
    LeftStickPress("buttonL3", R.string.button_l3),
    RightStickPress("buttonR3", R.string.button_r3),
    DpadUp("buttonDpadUp", R.string.button_dpad_up),
    DpadDown("buttonDpadDown", R.string.button_dpad_down),
    DpadLeft("buttonDpadLeft", R.string.button_dpad_left),
    DpadRight("buttonDpadRight", R.string.button_dpad_right);

    companion object {
        val orderedButtons: List<ControllerButton> = entries
    }
}

/**
 * Friendly names for the available virtual key codes that the Better XCloud script understands.
 * These map directly to the JavaScript KeyboardEvent "code" values.
 */
enum class VirtualKey(val code: String, val friendlyName: Int) {
    Enter("Enter", R.string.key_enter),
    Escape("Escape", R.string.key_escape),
    Space("Space", R.string.key_space),
    ArrowUp("ArrowUp", R.string.key_arrow_up),
    ArrowDown("ArrowDown", R.string.key_arrow_down),
    ArrowLeft("ArrowLeft", R.string.key_arrow_left),
    ArrowRight("ArrowRight", R.string.key_arrow_right),
    KeyA("KeyA", R.string.key_a),
    KeyB("KeyB", R.string.key_b),
    KeyC("KeyC", R.string.key_c),
    KeyD("KeyD", R.string.key_d),
    KeyE("KeyE", R.string.key_e),
    KeyF("KeyF", R.string.key_f),
    KeyG("KeyG", R.string.key_g),
    KeyH("KeyH", R.string.key_h),
    KeyI("KeyI", R.string.key_i),
    KeyJ("KeyJ", R.string.key_j),
    KeyK("KeyK", R.string.key_k),
    KeyL("KeyL", R.string.key_l),
    KeyM("KeyM", R.string.key_m),
    KeyN("KeyN", R.string.key_n),
    KeyO("KeyO", R.string.key_o),
    KeyP("KeyP", R.string.key_p),
    KeyQ("KeyQ", R.string.key_q),
    KeyR("KeyR", R.string.key_r),
    KeyS("KeyS", R.string.key_s),
    KeyT("KeyT", R.string.key_t),
    KeyU("KeyU", R.string.key_u),
    KeyV("KeyV", R.string.key_v),
    KeyW("KeyW", R.string.key_w),
    KeyX("KeyX", R.string.key_x),
    KeyY("KeyY", R.string.key_y),
    KeyZ("KeyZ", R.string.key_z),
    Digit1("Digit1", R.string.key_digit1),
    Digit2("Digit2", R.string.key_digit2),
    Digit3("Digit3", R.string.key_digit3),
    Digit4("Digit4", R.string.key_digit4);

    companion object {
        fun fromCode(code: String?): VirtualKey = entries.firstOrNull { it.code == code } ?: KeyK
    }
}

package com.betterxcloud.client.data

/**
 * Container for all user-configurable options. This is used both by the compose UI and by the
 * JavaScript bridge that injects the Better XCloud script.
 */
data class XCloudPreferences(
    val controllerKernel: ControllerKernel,
    val buttonMapping: Map<ControllerButton, VirtualKey>,
    val keepScreenAwake: Boolean,
) {
    companion object {
        /** Default configuration that closely mirrors the behavior of an Xbox controller. */
        fun default(): XCloudPreferences = XCloudPreferences(
            controllerKernel = ControllerKernel.Native,
            buttonMapping = mapOf(
                ControllerButton.A to VirtualKey.KeyK,
                ControllerButton.B to VirtualKey.KeyJ,
                ControllerButton.X to VirtualKey.KeyU,
                ControllerButton.Y to VirtualKey.KeyI,
                ControllerButton.LeftBumper to VirtualKey.KeyQ,
                ControllerButton.RightBumper to VirtualKey.KeyE,
                ControllerButton.LeftTrigger to VirtualKey.Digit1,
                ControllerButton.RightTrigger to VirtualKey.Digit3,
                ControllerButton.View to VirtualKey.Enter,
                ControllerButton.Menu to VirtualKey.Escape,
                ControllerButton.LeftStickPress to VirtualKey.KeyF,
                ControllerButton.RightStickPress to VirtualKey.KeyG,
                ControllerButton.DpadUp to VirtualKey.ArrowUp,
                ControllerButton.DpadDown to VirtualKey.ArrowDown,
                ControllerButton.DpadLeft to VirtualKey.ArrowLeft,
                ControllerButton.DpadRight to VirtualKey.ArrowRight,
            ),
            keepScreenAwake = true,
        )
    }
}

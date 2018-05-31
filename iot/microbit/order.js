let counter = 0
let id = ""
input.onButtonPressed(Button.A, () => {
    if (counter > 0) {
        counter += -1
        basic.showNumber(counter)
    }
})
input.onGesture(Gesture.Shake, () => {
    radio.sendValue(id, counter)
    basic.showString(id)
    basic.showNumber(counter)
})
input.onButtonPressed(Button.B, () => {
    if (counter < 9) {
        counter += 1
        basic.showNumber(counter)
    }
})
radio.setGroup(1)
id = "Name"
counter = 0
basic.showString(id)
basic.forever(() => {

})

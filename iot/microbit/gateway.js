radio.onDataPacketReceived( ({ receivedString: id, receivedNumber: product }) =>  {
    serial.writeValue(id, product)
    basic.showString(id)
    basic.showNumber(product)
})
serial.redirectToUSB()
radio.setGroup(1)
basic.showNumber(1)
basic.forever(() => {
	
})
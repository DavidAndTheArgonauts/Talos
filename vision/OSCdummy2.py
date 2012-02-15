import OSC


send_address = '127.0.0.1', 5500
send_address = '129.215.4.118', 5500

c = OSC.OSCClient()
c.connect( send_address )

bundle = OSC.OSCBundle()

bundle.append( {'addr':"/table/room", 'args':["big"]} )

bundle.append( {'addr':"/table/blue/visible", 'args':[1]} )
bundle.append( {'addr':"/table/blue/posx", 'args':[23.232] } )
bundle.append( {'addr':"/table/blue/posy", 'args':[45.345] } )
bundle.append( {'addr':"/table/blue/dirx", 'args':[0.89100652418836786235970957141363] } )
bundle.append( {'addr':"/table/blue/diry", 'args':[0.45399049973954679156040836635787] } )

bundle.append( {'addr':"/table/yellow/visible", 'args':[1] } )
bundle.append( {'addr':"/table/yellow/posx", 'args':[32.232] } )
bundle.append( {'addr':"/table/yellow/posy", 'args':[54.345] } )
bundle.append( {'addr':"/table/yellow/dirx", 'args':[0.45399049973954679156040836635787] } )
bundle.append( {'addr':"/table/yellow/diry", 'args':[0.89100652418836786235970957141363] } )

bundle.append( {'addr':"/table/red/visible", 'args':[1] } )
bundle.append( {'addr':"/table/red/posx", 'args':[60.52] } )
bundle.append( {'addr':"/table/red/posy", 'args':[30.2] } )

c.send(bundle)
c.close()
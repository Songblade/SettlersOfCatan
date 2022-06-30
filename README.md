# SettlersOfCatan
A simulation of Settlers of Catan. It was initially made for an end-of-the-semester project for our Data Structures class, though it was updated over the course of the following summer.

*Controls:* 
	'Space' - Pass Turn
	'Backspace' - Cancel Action / Decline Trade
	'Enter' - Confirm Trade / Accept Trade
	'1' - Build Road
	'2' - Build Settlement
	'3' - Build City
	'4' - Buy Development Card
	'Q' - Play Knight
	'W' - Play Year of Plenty
	'E' - Play Road Building
	'R' - Play Monopoly
	'A' - Trade With Bank
	'S' - Trade With Players
	
*Settlement Phase*
	Settlement Phase is the first phase of the game, in which players will take turns placing their initial settlements.
	The order in which players place their first settlements is chosen randomly.
	Once all players have places their first settlements, they will take turns placing their second settlements.
	The order in which players place their second settlements is the reverse of the order in which they placed their initial settlements.
	
	On a settlement turn:
		-The player will be prompted to place a settlement. This will not cost any resources.
		-After the player placed his settlement, the player will be prompted to place a road adjacent to the settlement. This will not cost any resources.
		-After the player placed his road, the turn will be passed.
		
*Turns*
	During the main gamepley phase, players will take turns performing actions.
	At the beginning of a turn, the dice will be rolled.
	Each will gain 1 resource for every settlement and 2 resources for every city adjacent to a hexagon when its number is rolled.
	Although the die is displayed as a single number, the number displayed is actually the sum of two random 1-6 rolls. (Therefore, numbers like 6 are more common than numbers like 11)
	Players may only build, buy or play development cards, trade with the bank, or propose trades to other players on their turns.
	Turns are passed with 'space' this is the only way to end a turn.
	Turns can not be passed while the thief is being moved on 7, or the player is awaiting responses to a trade request.

*Resources*
	Your resources will be displayed on the bottom left of the window.
	Your opponent's resource quantities will be displayed next to their respective icons on the left side of the window.
	Resources can be spent on structures or development cards using the '1', '2', '3', and '4' keys.

*7 and Robber*
	Whenever a 7 is rolled, the following will happen:
		-All players with more than 7 cards will be prompted to discard half of their cards. The game won't continue until all players have done so.
		-The player who rolled the 7 will be forced to move the robber.
		-If there are any adjacent settlements/cities to the robber's new hexagon, the player who rolled the 7 will pick an adjacent settlement to steal a resource from.
		-The game will continue. (It will be the player who rolled 7's turn.)

*Building:*
	Use keys '1', '2', and '3' to build. 
	Players may only build if you have the resources required to build your selected structure, there is an available spot to build your selected structure, and it is your turn.

*Development Cards:*
	Development cards can be purchased using '4'
	Players may only purchase development cards if it is their turn and there are development cards ramaining in the deck
	Development cards may not be played on the turn they were purchased on, or when it is not the player's turn.
	
*Ports and Bank Trading*
	Players can trade with the bank using 'A'
	Once a bank trade is initiated, select the resource you want to give, then selct the resource you want to get in exchange.
	By default, the bank will trade 4:1.
	If the player owns a settlement/city next to a "?" port, the bank will trade 3:1 with that player.
	If the player owns a settlement/city next to a resource port, the bank will trade 2:1 with that player if the resource he's giving is the one specified on the port.
	
*Player to Player Trading*
	-Proposing the Trade-
	
	Players can propose to trade with other players using 'S'
	Once the trade proposal is initiated, an icon will appear above the player's icon on the bottom left of the screen, and a blue "0" will appear above each resource icon.
	When the forementioned icon is green, selecting a resource will increase the number of that resource you will gain / decrease the number of that resource you will lose during the proposed trade.
	When the forementioned icon is red, selecting a resource will decrease the number of that resource you will gain / increase the number of that resource you will lose during the proposed trade.
	Red numbers above resource icons signify a loss during a trade, while green numbers above icons signify a gain during a trade.
	
	While proposing a trade, a player may control who he sends his trade request to.
	To prevent a trade request from reaching a player, the requesting player may click on the player's icon, which will toggle the green outline around his icon.
	Trades will only be sent to players with green outlines around their icons.
	
	-Sending the Trade-
	Once the trade proposal is complete, the proposing player may press enter to send his trade to other players.
	If no players can accept the trade, the proposing player's turn will resume. 
	If at least one player can accept the trade, the proposing player will be forced to wait until his request is declined by all reciving players or accepted by a player.
	If a trade is accepted, 
	
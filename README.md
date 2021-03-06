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
	During the main gameplay phase, players will take turns performing actions.
	At the beginning of a turn, the dice will be rolled.
	Each will gain 1 resource for every settlement and 2 resources for every city adjacent to a hexagon when its number is rolled.
	Although the die is displayed as a single number, the number displayed is actually the sum of two random 1-6 rolls. (Therefore, numbers like 6 are more common than numbers like 11)
	Players may only build, buy or play development cards, trade with the bank, or propose trades to other players on their turns.
	Turns are passed with 'space' this is the only way to end a turn.
	Turns can not be passed while the thief is being moved on 7, or the player is awaiting responses to a trade request.
	
*Board*
	The board is randomly generated each game.
	The board consists of 19 tiles, of containing:
		-4 wood tiles
		-4 sheep tiles
		-4 wheat tiles
		-3 ore tiles
		-3 brick tiles
		-1 desert tile
		
		-2 of each number 3-6
		-2 of each number 8-11
		-1 number 2
		-1 number 12
		
	6's and 8's can't be placed adjacent to each other.
	The desert tile's placement is random.

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
	Development cards are displayed on the bottom right of the window.
	Opponent's development card quantities are displayed to the right of their icons.
	
*Ports and Bank Trading*
	Players can trade with the bank using 'A'
	Once a bank trade is initiated, select the resource you want to give, then selct the resource you want to get in exchange.
	By default, the bank will trade 4:1.
	If the player owns a settlement/city next to a "?" port, the bank will trade 3:1 with that player.
	If the player owns a settlement/city next to a resource port, the bank will trade 2:1 with that player if the resource he's giving is the one specified on the port.
	
*Player to Player Trading*
	-Proposing the Trade-
	
	Players can propose to trade with other players using 'S'
	Once the trade proposal is initiated, an icon will appear above the player's icon on the bottom left of the window, and a blue "0" will appear above each resource icon.
	When the forementioned icon is green, selecting a resource will increase the number of that resource you will gain / decrease the number of that resource you will lose during the proposed trade.
	When the forementioned icon is red, selecting a resource will decrease the number of that resource you will gain / increase the number of that resource you will lose during the proposed trade.
	Clicking the formentioned icon will change it to red if it is green, and to green if it is red.
	Red numbers above resource icons signify a loss during a trade, while green numbers above icons signify a gain during a trade (From the requesting player's perspective).
	
	While proposing a trade, a player may control who he sends his trade request to.
	To prevent a trade request from reaching a player, the requesting player may click on the player's icon, which will toggle the green outline around his icon.
	Trades will only be sent to players with green outlines around their icons.
	
	-Sending the Trade-
	Once the trade proposal is complete, the proposing player may press enter to send his trade to other players.
	If no players can accept the trade, the proposing player's turn will resume. 
	If at least one player can accept the trade, the proposing player will be forced to wait until his request is declined by all reciving players or accepted by a player.
	If a trade is accepted, the resources specified in the trade will be exchanged and requesting player's turn will be resumed.
	
	-Responding to the Trade-
	Once a trade is proposed, all players who were specified on the trade's whitelist and can make the trade will recive the trade request.
	When a trade request is recived, numbers will appear above the player's resource icons.
	Red numbers above resource icons signify a loss during a trade, while green numbers above icons signify a gain during a trade (From the revciving player's perspective).
	Blue zeroes above resource icons signify no loss or gain.
	A trade request may be accepted with 'Enter' or declined with 'Backspace'.
	If a trade request is accepted by another player before the player accepts it, the player will lose the trade request.
	
*Achievements*
	Longest Road is held by the player who owns the longest chain of road tiles of at least 5 and is worth 2 victory points.
	Largest Army is held by the player who has played the most knight cards, with a minimum of 3. It is worth 2 victory points.
	Achievement cards and played knights are displayed at the bottom center of the window.
	Opponent's knights are displayed to the right of their icons.
	
*Winning the game*
	To win the game, you must be the first player to obtain 10 victory points
	
	Items which are worth victory points:
		Settlement         - 1
		City               - 2
		Victory Point Card - 1
		Longest Road       - 2
		Largest Army       - 2
		
	Upon winning the game, the application will close, and Shimmy will congradulate you on your victory.
		
*Catan Rules*
	For official Catan rules, please refer to https://www.catan.com/sites/default/files/2021-06/catan_base_rules_2020_200707.pdf
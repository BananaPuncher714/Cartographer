                                                                                   ,,                        
  .g8"""bgd                    mm                                               `7MM                        
.dP'     `M                    MM                                                 MM                        
dM'       ` ,6"Yb.  `7Mb,od8 mmMMmm ,pW"Wq.   .P"Ybmmm `7Mb,od8 ,6"Yb. `7MMpdMAo. MMpMMMb.  .gP"Ya `7Mb,od8 
MM         8)   MM    MM' "'   MM  6W'   `Wb :MI  I8     MM' "'8)   MM   MM   `Wb MM    MM ,M'   Yb  MM' "' 
MM.         ,pm9MM    MM       MM  8M     M8  WmmmP"     MM     ,pm9MM   MM    M8 MM    MM 8M""""""  MM     
`Mb.     ,'8M   MM    MM       MM  YA.   ,A9 8M          MM    8M   MM   MM   ,AP MM    MM YM.    ,  MM     
  `"bmmmd' `Moo9^Yo..JMML.     `Mbmo`Ybmd9'   YMMMMMb  .JMML.  `Moo9^Yo. MMbmmd'.JMML  JMML.`Mbmmd'.JMML.   
                                             6'     dP                   MM                                 
                                             Ybmmmd'                   .JMML.                               

This contains just about all the helpful stuff and things you'll need to know. Other modules may contain their
own READMEs, so be sure to check.

#############################
#         The Config
#############################

Cartographer config - Created by BananaPuncher714
The map will automagically save and reload itself whenever the server restarts.
It will also automagically adjust the zoom so you can't zoom too far out if the map itself is smaller.

Do NOT make the height or width smaller than 128!!
map:
    height: 512
    width: 512

The load speed is in chunks per tick. Five is the generally recommended amount.
    load-speed: 5

This is whether or not to allow updating of the map canvas. If false, placing and breaking blocks as well
as reloading the map will have no effect on the map. Useful for custom images as a minimap
updates: true
This is whether or not to mix transparent pixels when displaying an overlay
     fancy: true

This is whether the center of the map will be in the center of a chunk or your exact position
    center-chunk: true

This is whether or not to re-render a chunk after it is loaded. Not advised unless your
your server deals with lots of block updating and changing. It is better to write
a custom block updater, but use this if you have no choice.
    render-on-chunk-load: false


The DEFAULT color if there is no color for a material or if it's over the void
shading:
    default-color: "255, 0, 255"

This is how dark the water can get based on this formula: ( depth * shade ) / 100
The result is a percent that is used to darken the color water.
Of course, this also depends on the rgb value of the water color, but
too small a value will not show up on the map, and too high will completely dark out deep areas.
    water-shading: 100

This is the height at which the map will start shading.
-1 means it does not change anything
The default-renderer must be disabled for this to work! 
    shade-height: -1

This is whether to change the color to black if there is a non-transparent block above it
Only applies if map-height is NOT -1
    hide-if-hidden: true

This will show the highest block if the displayed block is NOT the highest.
Only applies if map-height is NOT -1
Overrides the hide-if-hidden option if true
    show-highest: true

This is how fast custom renderers will reload the CursorSelectors as specified by other plugins.
This is expressed in the amount of seconds before updating
misc:
    selector-load-delay: 0

This is whether the cursor will appear like a normal map
    show-player: true

This is for if you have the map bug where dying or changing worlds makes the map break
    map-bug: false

MODULES!!!
Here you can enable and disable what stuff you want
module:
    waypoints: true
    radar: true
    display: true

Here are extra presets that will get loaded in order after loading the default config colors.
Place them inside the presets folder.
If you do not want any presets then change the following lines to 'preset: []'
presets: []


This is if you have trouble with cartographer or if an errors are showing!
debug: false

#######################################
#            PERMISSIONS
#######################################

cartographer.admin - The admin command to everything apart from cursor and text selectors;
cartographer.main.inventory - Allows you to open the cartographer inventory;
cartographer.map.use - Allows you to use the map, true by default;

cartographer.waypoints.use - Allows you to use waypoints, true by default;
cartographer.waypoints.create - Allows you to create waypoints, true by default;
cartographer.waypoints.teleport - Lets you teleport to waypoints;
cartographer.waypoints.staff - Lets you toggle staff mode on waypoints;
cartographer.waypoints.public - Lets you toggle the public status on waypoints;
cartographer.waypoints.admin - Lets you do all waypoint stuff;
cartographer.waypoints.bypass.limit - Lets you bypass the maximum waypoint limit

cartographer.display.coordinates - Allows you to see the coordinates on the top left of the map;

cartographer.factionsuuid.cursors - Allows you to see other faction members as well as the faction home;

cartographer.kingdoms.cursors - Allows you to see other kingdom members as well as your kingdom home;

cartographer.radar.use - Lets you use the radar;

cartographer.towny.cursors - Lets you see town mates and the town home;

cartographer.locale.change - Lets you change your locale

cartographer.citizens.inventory - Lets you open the citizens inventory
cartographer.citizens.cursors - Lets you see the citizens on your map, true by default
cartographer.citizens.bypass.range - Lets you see all the citizens everywhere

cartographer.quests.cursors - Lets you see all your locations to reach, true by default
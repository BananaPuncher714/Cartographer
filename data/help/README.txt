Thank you for buying Cartographer!
Here's your user's manual below:

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

                                           
Cartographer user's manual:

Cartographer is split up into several parts:
- The master configs
- The presets
- The minimaps
- The fonts
- The locales
- The maps and overlays
- The modules

You probably won't need to use the fonts or the locales that much, so we won't go into detail about them here.
What you probably DO want to know is, "What the heck are these things and how do I use them?"
Well, here I'll be covering them in greater detail.

###########################################################################################################

The master configs:

The configs inside the '/configs/' folder are the master configs for newly created maps. The new map will
attempt to copy the configs inside the '/configs/' folder, and so will each new module of that map.
So, for example, if you wanted all the future maps to have a width of 1024, simply change the map.width
value in the master config to 1024 and any new maps created will have a width of 1024. This removes
the necessity to edit each map's config over and over again.

###########################################################################################################

The minimaps:

Cartographer now supports multiple maps per server, and world. You can easily create new maps in-game by using
"/setmapcenter <name>'. If the name already exists, you will simply set the center of that map without making
a new one. Otherwise, a new map will be created based off the master config and centered around your position.
Whenever you move off a map, Cartographer will try to match another map to you based off your position. If it
can't find one, it will simply display the amazing Gaben or whatever you set as the missing-map.ser file. You
can also reload each map with '/mapreload [name]', with the name being optional. Each map has its own individual
overlays, and can also use their own presets. Each map will be saved inside the '/saves/' folder with their id
as the name of the folder.

###########################################################################################################

The maps and overlays:

Whenever you download a new image to use as a map or an overlay for one, it gets saved automagically to the '/maps/' folder.
Each minimaps' folder also contains a '/maps/' folder, where you can place local overlays and images that only that minimap
can access. It's quite useful for sharing maps, if you ever want to...
The same works for presets.

###########################################################################################################

The modules:

Each minimap has its own modules, or addons, which can enhance or change the way the minimap operates. All
minimaps have their own unique modules, so waypoints that work in one minimap will NOT work in another.
Each module has its own master config inside the '/configs/' folder named '<module>-config.yml'. Each module saves
their unique information inside the minimaps' folder in the directory '/modules/'.

###########################################################################################################

The presets:

A preset is simply a YAML file containing two parts: A transparent blocks part, and a material part. The 
transparent blocks part lists all the blocks that should or should not be made invisible. The materials part
defines what color to make each block on the map before shading. Each minimap contains its own preset folder
and will use that first when searching for a preset specified in the config.

###########################################################################################################

The locales:

Cartographer features a robust, easy to use locale system. You can use '/cartographer locale' in-game to specify
which locale you would like to use. It is different for each player. You can create your own locale by copying the
'default_lang.yml' inside the '/locale/locales/' folder. That file contains more information on how to make
your own locale.

###########################################################################################################

The fonts:

This is a obscure feature, but very useful if you want to make your server look especially unique. You can create your own font
by copying the 'font-template.yml' file found inside the '/fonts/' folder. You can then specify the pixel layout for
each character with a grid. Any character that is not a space will become a pixel on the map. The display module supports
using custom fonts, and should reference the font per display with the 'font: <font id>' option.

###########################################################################################################

That's it for those explanations, but that's not all that Cartographer has to offer! For more content and things,
you can visit me(BananaPuncher714) on the Banana Support discord here: https://discord.gg/7mZQDEm
There, you can find overlays, presets, fonts, locales, and maps, or you can share your own!

The Cartographer wiki will also be updated in the future, so stay tuned!
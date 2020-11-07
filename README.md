WbsUtils is a library plugin that contains a variety of utilities that are shared across most of my other plugins.

Note that there are some components are unfinished, such as the ConfigurableField class which I never got around to finishing.

However, other areas I try to keep fairly clean and consistent; almost all of my other plugins use the plugin package as a framework for base classes, though I may move to an annotation system at some point. My Trail and Magic plugins use the particle system extensively, as it's flexible for both developers creating hard-coded effects, and for end users to configure in realtime.
# 🌟 JavaReflect Clone ~ DSA IN 3D

## Journey

### Phase 1
- Minimal Setup [Done]
- Pick minimal shapes material no gui [Done]
- Generate boxes based on array inputs [Done]
- every box is associated with a value show that [Done]
- fix bugs make numbers look beautiful [Done]
- make a line of chain between the boxes [Done]
- show insertion animation in array [Done]
- implement video generation using ffmpeg [Done]
- clean code and give a better structure to the code base
- delete animations (swap too) [This is challenging]
- generate video

#### Some Thoughts before moving to Phase 2 :

```
 I want to give a better structure to my 3D Visualisation project. The project is not going to be an ordinary one, it is going to be the most ultimate one, with ray tracing implementations, and 3D video generation just by calling functions in code. And nothing complex. The use of this library will be almost be the exact copy of how collections framework in java is used. For example, here we take an ArrayList, provided that the programmer goes for calling .add and .remove functions, these functions will implement animations underlying and showing the manipulations through ray tracing. But here's the catch, we are not going to show any live animations or representations right on the spot, we are just going to generate a sweet video and open it for them. That we've already done as you know. What is left for now is the right structure, the right classes, the right design, and where and how happens what? It should be become a well planned out library. So that programmers and educational professors should just hit import and use it the same way they use an collections framework of Java, just the change is that these classes are named JArrayList<> so we are adding a J before every class name, pretty easy right? But here's the challenge, the animation changes, manual frame handling, and camera changes bit by bit to make it look perfect has become so complex and messy, even for showing one operation, that it takes a lot and literally a lot of time, to just add a function of a class, suppose it took me a day just to show insert at position function in an simple array, and that also isn't perfect. Now, I have told you everything, please guide me from now. Let's see what you got ! Push yourself buddy !
 
And I am gonna build the bonus tools like scene builder and stuff ! But before starting up to advance the project ! There's an issue over here, that our 3D Visualizations are full of mirror reflections and a HDRI background of mountains and lake, though it looks very beautiful, but suppose I want to show an array size of 100, or even 100 elements, how would that every fit in a frame or photo, the boxes beyond an array size of 5 looks so small, that their numbers are unreadable, so static image generation is not possible here, only video and stuff ! But imagine the user asked for the showing the visualizing the values of data structure first before calling any functions, he called JArrayList<Integer> arr = new JArrayList<>(), and then he called, arr.show() or arr.visualize() then what do I do? Another issue is that a 2D visualisation is clearly visible from a white board background perspective, though it doesn't look beautiful, how would I able to balance the beauty of my 3D Visualisation and clear visibility underneath together? These are my current challenges?

It is going to add this value to the world [with value displayed on it]. By default, the new value will always come sliding from left. And it will take the starting position it is currently on then increment. What we have to do is make sure that. We are separating camera animations with box animations. And for that we are going to create two separate classes for that

Functions of this class will be internally called ! We start by going into the defaults of this ! Let's call it from JArrayList. Screen size will be common. Camera will be unique for every animator. And renderer engine will be unique as well. World itself will be unique to each array we create

```

### Phase 2
- Create a separate JArrayListAnimator class (Separate animation logic) [Done]
- Simplify animation logic using separate cameraAnimator and boxAnimator class [Done]
- Add both add and remove operation animations for a simple ArrayList [Done]
- Simply video generation for every data structure [On going . . .]

```
Rendering Optimizations are very much required as current speed is 200ms for every frame, and for a simple add and remove operation on arraylist, it needs 500 frames for a size of 10 values, and that literally needs us to wait a lot, which nobody would actually want to. Fast usability is more necessary here. The challenge is how that would be done?
```

### Phase 3
- Extreme optimizations are needed for rendering ! 
- Implemented BVH from scratch and it didn't boosted rendering time !

Decided to focus on implementing data structure and animations first !
Build the GUI and simple progress bar because we have no option ! Rendering time is a lot.
Then ask for a live interaction mode ! Where rendering happens and is shown to user at the same time !
[ Remember to keep stuff super simple ! ]

Making the rendering fast is most important
Then adding live interaction mode !
Then adding more data structures !
Then adding animations and GUI !
Then adding video generation !
Then adding scene builder !

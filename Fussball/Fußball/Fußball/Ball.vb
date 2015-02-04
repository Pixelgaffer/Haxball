Public Class Ball
    Inherits Entity


    Sub New(pos As Point)
        MyBase.New(pos, New Vector(0, 0))
        color = Colors.Black
        size = 20
        friction = 0.04
    End Sub

    Sub New(pos As Point, s As Vector)
        MyBase.New(pos, s)
        color = Colors.Black
        size = 20
        friction = 0.04
    End Sub



    Overrides Function draw() As List(Of UIElement)
        Return MyBase.draw()
    End Function




End Class

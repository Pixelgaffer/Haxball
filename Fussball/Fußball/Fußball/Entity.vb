Public Class Entity

    Public position As New Point(0, 0)
    Public speed As New Vector(0, 0)
    Public size As Double = 40
    Public color As Color = Colors.Blue
    Public friction As Double = 0.3


    Sub New()

    End Sub

    Sub New(pos As Point, s As Vector)
        position = pos
        speed = s
    End Sub



    Sub move()
        position += speed
        speed *= 1 - friction
    End Sub


    Overridable Function draw() As List(Of UIElement)
        Dim list As New List(Of UIElement)
        Dim circle As New Ellipse()
        circle.Fill = New SolidColorBrush(color)
        Canvas.SetLeft(circle, position.X - (size / 2))
        Canvas.SetTop(circle, position.Y - (size / 2))
        circle.Width = size
        circle.Height = size
        list.Add(circle)

        Return list
    End Function


End Class

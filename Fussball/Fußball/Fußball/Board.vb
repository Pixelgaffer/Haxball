Public Class Board

    Public size As Double = 800
    Public players As New List(Of Player)
    Public ball As Ball
    Public scores(1) As Integer

    Private random As New Random()



    Sub New()
        ball = New Ball(New Point(size / 2, size / 4))
    End Sub

    Sub New(s As Double)
        size = s
        ball = New Ball(New Point(size / 2, size / 4))
    End Sub


    Sub addPlayer()
        Dim team As Boolean = False
        Dim counter As Integer = 0
        For Each p In players  'teams anschauen
            If (p.team) Then
                counter += 1
            Else
                counter -= 1
            End If
        Next
        If counter < 0 Then  'neuen Spieler sinnvoll einem Team hinzufügen
            team = True
        End If

        players.Add(New Player(size, team, random))

    End Sub

    Sub addPlayer(player As Player)
        players.Add(player)
    End Sub


    Sub move()
        For Each p In players
            p.checkMove()
            If p.doesShoot Then  'schießt
                Dim dist As Vector = p.position - ball.position
                If dist.Length <= p.shootDistance Then  'innerhalb der Distanz
                    Dim acceleration As Vector = (dist / dist.Length * -p.shootForce)
                    ball.speed += acceleration
                End If
            End If
            p.move()
        Next
        ball.move()

        checkCollisions()
    End Sub


    Sub checkCollisions()

        'Randkollision
        For Each p In players  'Spieler-Rand
            If p.position.X < p.size / 2 Then
                p.speed.X *= -1
                p.position.X = p.size / 2
            End If
            If p.position.X > size - p.size / 2 Then
                p.speed.X *= -1
                p.position.X = size - p.size / 2
            End If
            If p.position.Y < p.size / 2 Then
                p.speed.Y *= -1
                p.position.Y = p.size / 2
            End If
            If p.position.Y > size / 2 - p.size / 2 Then
                p.speed.Y *= -1
                p.position.Y = size / 2 - p.size / 2
            End If
        Next

        'Ball-Rand
        If ball.position.X < ball.size / 2 Then
            ball.speed.X *= -1
            ball.position.X = ball.size / 2
            If ball.position.Y > size / 8 And ball.position.Y < size * 3 / 8 Then
                goal(True)
            End If
        End If
        If ball.position.X > size - ball.size / 2 Then
            ball.speed.X *= -1
            ball.position.X = size - ball.size / 2
            If ball.position.Y > size / 8 And ball.position.Y < size * 3 / 8 Then
                goal(False)
            End If
        End If
        If ball.position.Y < ball.size / 2 Then
            ball.speed.Y *= -1
            ball.position.Y = ball.size / 2
        End If
        If ball.position.Y > size / 2 - ball.size / 2 Then
            ball.speed.Y *= -1
            ball.position.Y = size / 2 - ball.size / 2
        End If



        'zwischenmenschliche Kollision
        For Each p1 In players
            For Each p2 In players
                If Not p1.Equals(p2) Then
                    Dim dist As Vector = p1.position - p2.position
                    If dist.Length < p1.size / 2 + p2.size / 2 Then
                        Dim backLength As Double = (p1.size / 2 + p2.size / 2 - dist.Length) / 50  'keine Ahnung, warum so wenig
                        p1.position += dist * backLength
                        p2.position -= dist * backLength
                    End If
                End If
            Next
        Next
        'Ball-Spieler Kollision
        For Each p1 In players
            Dim dist As Vector = p1.position - ball.position
            If dist.Length < p1.size / 2 + ball.size / 2 Then
                Dim backLength As Double = (p1.size / 2 + ball.size / 2 - dist.Length) / 50  'keine Ahnung, warum so wenig
                p1.position += dist * backLength
                ball.position -= dist * backLength
            End If
        Next
    End Sub


    Sub goal(winner As Boolean)
        Select Case winner
            Case False
                scores(0) += 1
            Case True
                scores(1) += 1
        End Select

        'zurücksetzen
        ball.position = New Point(size / 2, size / 4)
        ball.speed = New Vector(0, 0)
        For Each p In players
            p.randomPos(size, random)
            p.speed = New Vector(0, 0)
        Next
    End Sub



    Function draw() As List(Of UIElement)
        Dim list As New List(Of UIElement)

        'Spielstandsanzeige
        Dim score1, score2 As New Label()
        score1.FontSize = 25
        score1.Foreground = New SolidColorBrush(players(0).color)
        score2.FontSize = 25
        score2.Foreground = New SolidColorBrush(players(1).color)
        score1.Content = scores(0)
        score2.Content = scores(1)
        Canvas.SetTop(score1, 10)
        Canvas.SetLeft(score1, size / 2 - size / 20)
        Canvas.SetTop(score2, 10)
        Canvas.SetLeft(score2, size / 2 + size / 20 - 25)
        score1.Width = 50
        score1.Height = 40
        score2.Width = 50
        score2.Height = 40
        list.Add(score1)
        list.Add(score2)

        'Spielfeld und mittlere Linie
        Dim field1 As New Rectangle()
        Dim field2 As New Rectangle()
        Canvas.SetTop(field1, 0)
        Canvas.SetLeft(field1, 0)
        Canvas.SetTop(field2, 0)
        Canvas.SetLeft(field2, size / 2)
        field1.Width = size / 2
        field1.Height = size / 2
        field2.Width = size / 2
        field2.Height = size / 2
        list.Add(field1)
        list.Add(field2)

        'Mittelkreis
        Dim middleCircle As New Ellipse()
        Canvas.SetTop(middleCircle, size / 4 - size / 16)
        Canvas.SetLeft(middleCircle, size / 2 - size / 16)
        middleCircle.Width = size / 8
        middleCircle.Height = size / 8
        middleCircle.Fill = New SolidColorBrush(Colors.White)
        list.Add(middleCircle)

        'Tore
        Dim gate1 As New Rectangle()
        Dim gate2 As New Rectangle()
        Canvas.SetTop(gate1, size / 8)
        Canvas.SetLeft(gate1, 0)
        Canvas.SetTop(gate2, size / 8)
        Canvas.SetLeft(gate2, size - size / 100)
        gate1.Width = size / 100
        gate1.Height = size / 4
        gate2.Width = size / 100
        gate2.Height = size / 4
        gate1.Fill = New SolidColorBrush(Colors.Black)
        gate2.Fill = New SolidColorBrush(Colors.Black)
        list.Add(gate1)
        list.Add(gate2)



        move()
        list.AddRange(ball.draw())
        For Each p In players
            list.AddRange(p.draw())
        Next
        Return list
    End Function


End Class

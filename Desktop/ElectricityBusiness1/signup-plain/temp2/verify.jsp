<!DOCTYPE html>
<html><body>
<h2>Validation du compte</h2>
<form method="post" action="verify">
  <label>Email <input type="email" name="email" required></label><br/>
  <label>Code reçu <input type="text" name="code" pattern="\\d{6}" required></label><br/>
  <button type="submit">Valider</button>
</form>
<p><a href="register">Retour inscription</a></p>
</body></html>
